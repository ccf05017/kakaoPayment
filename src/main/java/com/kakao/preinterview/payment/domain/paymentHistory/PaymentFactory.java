package com.kakao.preinterview.payment.domain.paymentHistory;

import java.math.BigDecimal;

public class PaymentFactory {
    public static PaymentHistory createPaymentAutoTax(
            int installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            Integer duration,
            Integer cvc,
            String key
    ) throws Exception {
        return new PaymentHistory(
                null,
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                EncryptedCardInfo.create(CardInfo.create(cardNumber, duration, cvc), key),
                Tax.autoCreate(payAmount)
        );
    }

    public static PaymentHistory createPaymentManualTax(
            int installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            Integer duration,
            Integer cvc,
            String key,
            Long taxAmount
    ) throws Exception {
        return new PaymentHistory(
                null,
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                EncryptedCardInfo.create(CardInfo.create(cardNumber, duration, cvc), key),
                Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount)
        );
    }

    public static PaymentHistory createPaymentCancelAllByAutoTax(PaymentHistory paymentHistory) {
        return new PaymentHistory(
                null,
                ManagementNumber.create(),
                paymentHistory.getManagementNumber(),
                PayInfo.create(paymentHistory.getInstallmentMonths(), paymentHistory.getPayAmount(), PayStatus.PAY_CANCEL),
                paymentHistory.getEncryptedCardInfo(),
                paymentHistory.getTax()
        );
    }

    public static PaymentHistory createPaymentCancelAllByManualTax(PaymentHistory paymentHistory, BigDecimal taxValue) {
        return new PaymentHistory(
                null,
                ManagementNumber.create(),
                paymentHistory.getManagementNumber(),
                PayInfo.create(paymentHistory.getInstallmentMonths(), paymentHistory.getPayAmount(), PayStatus.PAY_CANCEL),
                paymentHistory.getEncryptedCardInfo(),
                Tax.createManualCancelAllTax(paymentHistory.getTax(), taxValue)
        );
    }
}
