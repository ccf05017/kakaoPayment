package com.kakao.preinterview.payment.domain.payment;

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
            String key,
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

    public static Payment createPaymentCancelAllByAutoTax(Payment payment) {
        return new Payment(
                ManagementNumber.create(),
                payment.getManagementNumber(),
                PayInfo.create(payment.getInstallmentMonth(), payment.getPayAmount(), PayStatus.PAY_CANCEL),
                payment.getCardInfo(),
                payment.getTax()
        );
    }

    public static Payment createPaymentCancelAllByManualTax(Payment payment, BigDecimal taxValue) {
        return new Payment(
                ManagementNumber.create(),
                payment.getManagementNumber(),
                PayInfo.create(payment.getInstallmentMonth(), payment.getPayAmount(), PayStatus.PAY_CANCEL),
                payment.getCardInfo(),
                Tax.createManualCancelAllTax(payment.getTax(), taxValue)
        );
    }
}
