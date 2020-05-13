package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

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

    @DisplayName("부가가치세는 0 ~ 결제금액만큼의 범위로만 생성할 수 있다. (null로 생성할 수 없다)")
    @ParameterizedTest
    @NullSource
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

    @DisplayName("동등 비교가 가능해야한다.")
    @Test
    void equalTest() {
        Tax tax1 = Tax.manualCreate(value, payAmount);
        Tax tax2 = Tax.manualCreate(value, payAmount);
        assertThat(tax1).isEqualTo(tax2);
    }

    @DisplayName("세금을 자동으로 생성 시 소수점 이하 반올림해서 계산한다.")
    @Test
    void autoCreateTest() {
        assertThat(Tax.autoCreate(payAmount)).isEqualTo(BigDecimal.valueOf(91));
    }

    @DisplayName("null이나 0이하의 값으로 세금을 자동 생성할 수 없다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("invalidPayAmount")
    void autoCreateFailByInvalidPayAmount(BigDecimal invalidPayAmount) {
        assertThatThrownBy(() -> Tax.autoCreate(invalidPayAmount)).isInstanceOf(IllegalArgumentException.class);
    }
    public static Stream<BigDecimal> invalidPayAmount() {
        return Stream.of(
                BigDecimal.ZERO,
                BigDecimal.valueOf(-1)
        );
    }
}
