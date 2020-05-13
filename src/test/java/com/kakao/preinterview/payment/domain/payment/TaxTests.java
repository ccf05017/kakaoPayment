package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TaxTests {
    private BigDecimal value = BigDecimal.valueOf(100);
    private BigDecimal payAmount = BigDecimal.valueOf(1000);

    @DisplayName("값을 전달해서 수동으로 객체를 생성할 수 있다.")
    @Test
    void createTest() {
        assertThat(Tax.manualCreate(value, payAmount)).isNotNull();
    }
}
