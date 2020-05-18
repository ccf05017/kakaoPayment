package com.kakao.preinterview.payment.domain.service;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.*;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayCancelAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentPartialCancelService {
    public Payment doByAutoTax(PaymentHistory paymentHistory, String key, PayCancelRequestDto resource,
                               BigDecimal amountRemainSum, BigDecimal taxRemainSum) throws Exception {
        paymentHistoryPayAmountValidation(resource.getCancelAmount(), paymentHistory.getPayAmount());
        paymentHistoryPayRemainValidation(resource.getCancelAmount(), amountRemainSum);

        if (isFinalPartialCancel(resource.getCancelAmount(), amountRemainSum)) {
            paymentPartialCancelFinalAutoTaxValidation(resource, taxRemainSum);
        }

        return new Payment(
                ManagementNumber.create(),
                ManagementNumber.createFromPaymentHistory(paymentHistory),
                PayInfo.create(
                        InstallmentMonth.LUMPSUM,
                        resource.getCancelAmount(),
                        PayType.PAY_PARTIAL_CANCEL
                ),
                CardInfo.createFromDecryptedRawString(
                        getDecryptedRawCardInfoFromPaymentHistory(paymentHistory, key)
                ),
                autoTaxCalculator(resource, taxRemainSum)
        );
    }

    public Payment doByManualTax(PaymentHistory paymentHistory, String key, PayCancelRequestDto resource,
                                 BigDecimal amountRemainSum, BigDecimal taxRemainSum) {
        paymentHistoryPayAmountValidation(resource.getCancelAmount(), paymentHistory.getPayAmount());
        paymentHistoryPayRemainValidation(resource.getCancelAmount(), amountRemainSum);
        return null;
    }

    protected boolean isFinalPartialCancel(BigDecimal requestValue, BigDecimal remainSum) {
        return remainSum.equals(requestValue);
    }

    protected void paymentHistoryPayAmountValidation(BigDecimal requestValue, BigDecimal paymentHistoryPayAmount) {
        if (requestValue.compareTo(paymentHistoryPayAmount) >= 0) throw new InvalidPayCancelAmountException();
    }

    protected void paymentHistoryPayRemainValidation(BigDecimal requestValue, BigDecimal remains) {
        if (requestValue.compareTo(remains) > 0) throw new InvalidPayCancelAmountException();
    }

    protected void paymentPartialCancelFinalAutoTaxValidation(PayCancelRequestDto resource, BigDecimal taxRemainSum) {
        Tax autoCreatedTax = Tax.autoCreate(resource.getCancelAmount());
        if (autoCreatedTax.getValue().compareTo(taxRemainSum) < 0) throw new InvalidTaxAmountException();
    }

    private static String getDecryptedRawCardInfoFromPaymentHistory(
            PaymentHistory paymentHistory, String key
    ) throws Exception {
        return EncryptedCardInfo.decryptFromRawData(paymentHistory.getEncryptedCardInfo(), key);
    }

    private Tax autoTaxCalculator(PayCancelRequestDto resource, BigDecimal taxRemainSum) {
        BigDecimal autoCreatedTaxValue = Tax.autoCreate(resource.getCancelAmount()).getValue();
        BigDecimal resultValue = (autoCreatedTaxValue.compareTo(taxRemainSum) > 0) ? taxRemainSum : autoCreatedTaxValue;
        return Tax.manualCreate(resultValue, resource.getCancelAmount());
    }
}
