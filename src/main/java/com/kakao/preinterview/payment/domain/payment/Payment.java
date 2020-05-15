package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Payment {
    private Long id;
    private ManagementNumber managementNumber;
    private PayInfo payInfo;
    private EncryptedCardInfo encryptedCardInfo;
    private Tax tax;

    private Payment(
            Long id,
            ManagementNumber managementNumber,
            PayInfo payInfo,
            EncryptedCardInfo encryptedCardInfo,
            Tax tax
    ) {
        this.id = id;
        this.managementNumber = managementNumber;
        this.payInfo = payInfo;
        this.encryptedCardInfo = encryptedCardInfo;
        this.tax = tax;
    }

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
                PayInfo.create(installmentMonths, payAmount, payStatus),
                EncryptedCardInfo.create(CardInfo.create(cardNumber, duration, cvc), key),
                Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount)
        );
    }

    public Tax getTax() {
        return tax;
    }
}
