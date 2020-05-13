package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayInfoTests {
    private int installmentMonths = 12;
    private BigDecimal payAmount = BigDecimal.valueOf(1000);
    private PayStatus payStatus = PayStatus.PAY;

    @DisplayName("할부개월수, 결제금액, 결제 타입을 입력 받아서 객체를 생성할 수 있다.")
    @Test
    void createTest() {
        assertThat(PayInfo.create(installmentMonths, payAmount, payStatus)).isNotNull();
    }

    @DisplayName("0 ~ 12의 범위를 벗어난 할부개월수로는 객체를 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 13})
    void createFailByInvalidInstallmentMonths(int invalidInstallmentMonths) {
        assertThatThrownBy(() -> PayInfo.create(invalidInstallmentMonths, payAmount, payStatus))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("100원 미만이나 10억 초과의 결제 금액으로는 객체를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidPayAmount")
    void createFailByInvalidPayAmount(BigDecimal invalidPayAmount) {
        assertThatThrownBy(() -> PayInfo.create(installmentMonths, invalidPayAmount, payStatus))
                .isInstanceOf(IllegalArgumentException.class);
    }
    public static Stream<BigDecimal> invalidPayAmount() {
        return Stream.of(
                BigDecimal.valueOf(99),
                BigDecimal.valueOf(1000000001),
                BigDecimal.valueOf(100000000000L)
        );
    }
}
