package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Payment {
    private Long id;
    private PayInfo payInfo;
    private EncryptedCardInfo encryptedCardInfo;
    private Tax tax;

    private Payment(Long id, PayInfo payInfo, EncryptedCardInfo encryptedCardInfo, Tax tax) {
        this.id = id;
        this.payInfo = payInfo;
        this.encryptedCardInfo = encryptedCardInfo;
        this.tax = tax;
    }

    public Payment(
            int installmentMonths,
            BigDecimal payAmount,
            PayStatus payStatus,
            Long cardNumber,
            Integer duration,
            Integer cvc,
            String key
    ) throws Exception {
        this(
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                EncryptedCardInfo.create(CardInfo.create(cardNumber, duration, cvc), key),
                Tax.autoCreate(payAmount)
        );
    }

    public Tax getTax() {
        return tax;
    }
}
