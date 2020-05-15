package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class PaymentFactory {
    public static Payment createPaymentAutoTax(
            int installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            Integer duration,
            Integer cvc,
            String key
    ) throws Exception {
        return new Payment (
                null,
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                EncryptedCardInfo.create(CardInfo.create(cardNumber, duration, cvc), key),
                Tax.autoCreate(payAmount)
        );
    }

    public static Payment createPaymentManualTax(
            int installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            Integer duration,
            Integer cvc,
            String key,
            Long taxAmount
    ) throws Exception {
        return new Payment (
                null,
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                EncryptedCardInfo.create(CardInfo.create(cardNumber, duration, cvc), key),
                Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount)
        );
    }

    public static Payment createPaymentCancelAllByAutoTax(Payment payment) {
        return new Payment(
                null,
                ManagementNumber.create(),
                payment.getManagementNumber(),
                PayInfo.create(payment.getInstallmentMonths(), payment.getPayAmount(), PayStatus.PAY_CANCEL),
                payment.getEncryptedCardInfo(),
                payment.getTax()
        );
    }

    public static Payment createPaymentCancelAllByManualTax(Payment payment, BigDecimal taxValue) {
        return new Payment(
                null,
                ManagementNumber.create(),
                payment.getManagementNumber(),
                PayInfo.create(payment.getInstallmentMonths(), payment.getPayAmount(), PayStatus.PAY_CANCEL),
                payment.getEncryptedCardInfo(),
                Tax.createManualCancelAllTax(payment.getTax(), taxValue)
        );
    }
}
