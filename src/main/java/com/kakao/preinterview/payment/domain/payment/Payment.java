package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Payment {
    private Long id;
    private PayInfo payInfo;
    private String encryptedCardInfo;
    private Tax tax;

    private Payment(Long id, PayInfo payInfo, String encryptedCardInfo, Tax tax) {
        this.id = id;
        this.payInfo = payInfo;
        this.encryptedCardInfo = encryptedCardInfo;
        this.tax = tax;
    }

    public Payment(int installmentMonths, BigDecimal payAmount, PayStatus payStatus, String encryptedCardInfo) {
        this(
                null,
                PayInfo.create(installmentMonths, payAmount, payStatus),
                encryptedCardInfo,
                Tax.autoCreate(payAmount)
        );
    }

    public Tax getTax() {
        return tax;
    }
}
