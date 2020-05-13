package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @DisplayName("올바르지 못한 인자로 객체를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidParams")
    void validationTest(int invalidInstallmentMonths, BigDecimal invalidPayAmount) {
        assertThatThrownBy(() -> PayInfo.create(invalidInstallmentMonths, invalidPayAmount, payStatus))
                .isInstanceOf(IllegalArgumentException.class);
    }
    public static Stream<Arguments> invalidParams() {
        return Stream.of(
                // invalid installmentMonths
                Arguments.of(-1, BigDecimal.valueOf(1000)),
                Arguments.of(13, BigDecimal.valueOf(1000)),

                // invalid payAmount
                Arguments.of(12, BigDecimal.valueOf(99)),
                Arguments.of(12, BigDecimal.valueOf(1000000001)),
                Arguments.of(12, BigDecimal.valueOf(100000000000L))
        );
    }
}
