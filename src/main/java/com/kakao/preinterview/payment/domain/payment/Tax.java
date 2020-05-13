package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Tax {
    private BigDecimal value;

    private Tax(BigDecimal value) {
        this.value = value;
    }

    public static Tax manualCreate(BigDecimal value, BigDecimal payAmount) {
        return new Tax(value);
    }
}
