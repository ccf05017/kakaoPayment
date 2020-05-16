package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.exceptions.TryCancelFromCanceledPaymentException;

import java.math.BigDecimal;

public class PaymentFactory {
    public static Payment createPaymentAutoTax(
            InstallmentMonth installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            String duration,
            Integer cvc
    ) {
        CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);

        return new Payment(
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                cardInfo,
                Tax.autoCreate(payAmount)
        );
    }

    public static Payment createPaymentManualTax(
            InstallmentMonth installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            String duration,
            Integer cvc,
            Long taxAmount
    ) {
        CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);

        return new Payment(
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                cardInfo,
                Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount)
        );
    }

    public static Payment createPaymentCancelAllByAutoTax(PaymentHistory paymentHistory, String key) throws Exception {
        validateCanceled(paymentHistory);

        return new Payment(
                ManagementNumber.create(),
                ManagementNumber.createFromPaymentHistory(paymentHistory),
                PayInfo.create(
                        InstallmentMonth.createFromFormatMonth(paymentHistory.getInstallmentMonthFormatMonth()),
                        paymentHistory.getPayAmount(),
                        PayStatus.PAY_CANCEL
                ),
                CardInfo.createFromDecryptedRawString(
                        getDecryptedRawCardInfoFromPaymentHistory(paymentHistory, key)
                ),
                Tax.createFromPaymentHistory(paymentHistory)
        );
    }

    public static Payment createPaymentCancelAllByManualTax(
            PaymentHistory paymentHistory,
            String key,
            BigDecimal taxValue
    ) throws Exception {
        validateCanceled(paymentHistory);

        return new Payment(
                ManagementNumber.create(),
                ManagementNumber.createFromPaymentHistory(paymentHistory),
                PayInfo.create(
                        InstallmentMonth.createFromFormatMonth(paymentHistory.getInstallmentMonthFormatMonth()),
                        paymentHistory.getPayAmount(),
                        PayStatus.PAY_CANCEL
                ),
                CardInfo.createFromDecryptedRawString(
                        getDecryptedRawCardInfoFromPaymentHistory(paymentHistory, key)
                ),
                Tax.createManualCancelAllTax(Tax.createFromPaymentHistory(paymentHistory), taxValue)
        );
    }

    private static String getDecryptedRawCardInfoFromPaymentHistory(
            PaymentHistory paymentHistory, String key
    ) throws Exception {
        return EncryptedCardInfo.decryptFromRawData(paymentHistory.getEncryptedCardInfo(), key);
    }

    private static void validateCanceled(PaymentHistory paymentHistory) {
        if (paymentHistory.isCanceled()) throw new TryCancelFromCanceledPaymentException();
        if (PayStatus.PAY_CANCEL.getName().equals(paymentHistory.getPaymentStatusName()))
            throw new TryCancelFromCanceledPaymentException();
    }
}
