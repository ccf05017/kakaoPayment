package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Tax {
    private BigDecimal value;

    private Tax(BigDecimal value) {
        this.value = value;
    }

    public static Tax manualCreate(BigDecimal value, BigDecimal payAmount) {
        manualValidation(value, payAmount);
        return new Tax(value);
    }

    private static void manualValidation(BigDecimal value, BigDecimal payAmount) {
        if (BigDecimal.ZERO.compareTo(value) > 0 || payAmount.compareTo(value) < 0)
            throw new IllegalArgumentException();
    }
}
