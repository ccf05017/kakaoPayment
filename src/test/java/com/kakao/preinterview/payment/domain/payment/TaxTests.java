package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaxTests {
    private BigDecimal value = BigDecimal.valueOf(100);
    private BigDecimal payAmount = BigDecimal.valueOf(1000);

    @DisplayName("값을 전달해서 수동으로 객체를 생성할 수 있다.")
    @Test
    void createTest() {
        assertThat(Tax.manualCreate(value, payAmount)).isNotNull();
    }

    @DisplayName("부가가치세는 0 ~ 결제금액만큼의 범위로만 생성할 수 있다.")
    @ParameterizedTest
    @MethodSource("invalidValues")
    void createValidationTest(BigDecimal invalidValues) {
        assertThatThrownBy(() -> Tax.manualCreate(invalidValues, payAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }
    public static Stream<BigDecimal> invalidValues() {
        return Stream.of(
                BigDecimal.valueOf(-1),
                BigDecimal.valueOf(1001)
        );
    }
}
