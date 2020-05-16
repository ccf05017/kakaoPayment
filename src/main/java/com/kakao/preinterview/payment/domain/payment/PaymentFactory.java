package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class PaymentFactory {
    public static Payment createPaymentAutoTax(
            InstallmentMonth installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            String duration,
            Integer cvc,
            String key
    ) throws Exception {
        CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);

        return new Payment(
                null,
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                cardInfo,
                EncryptedCardInfo.create(cardInfo, key),
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
            String key,
            Long taxAmount
    ) throws Exception {
        CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);

        return new Payment(
                null,
                ManagementNumber.create(),
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                cardInfo,
                EncryptedCardInfo.create(cardInfo, key),
                Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount)
        );
    }

    public static Payment createPaymentCancelAllByAutoTax(Payment payment) {
        return new Payment(
                null,
                ManagementNumber.create(),
                payment.getManagementNumber(),
                PayInfo.create(payment.getInstallmentMonth(), payment.getPayAmount(), PayStatus.PAY_CANCEL),
                payment.getCardInfo(),
                payment.getEncryptedCardInfo(),
                payment.getTax()
        );
    }

    public static Payment createPaymentCancelAllByManualTax(Payment payment, BigDecimal taxValue) {
        return new Payment(
                null,
                ManagementNumber.create(),
                payment.getManagementNumber(),
                PayInfo.create(payment.getInstallmentMonth(), payment.getPayAmount(), PayStatus.PAY_CANCEL),
                payment.getCardInfo(),
                payment.getEncryptedCardInfo(),
                Tax.createManualCancelAllTax(payment.getTax(), taxValue)
        );
    }
}
