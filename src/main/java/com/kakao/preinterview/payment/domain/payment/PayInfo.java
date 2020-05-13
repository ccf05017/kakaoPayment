package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class PayInfo {
    private Integer installmentMonths;
    private BigDecimal payAmount;
    private PayStatus payStatus;

    private PayInfo(int installmentMonths, BigDecimal payAmount, PayStatus payStatus) {
        this.installmentMonths = installmentMonths;
        this.payAmount = payAmount;
        this.payStatus = payStatus;
    }

    public static PayInfo create(int installmentMonths, BigDecimal payAmount, PayStatus payStatus) {
        return new PayInfo(installmentMonths, payAmount, payStatus);
    }
}
