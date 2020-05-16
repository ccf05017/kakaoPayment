package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayInfoTests {
    private InstallmentMonth installmentMonths = InstallmentMonth.TWELVE;
    private BigDecimal payAmount = BigDecimal.valueOf(1000);
    private PayStatus payStatus = PayStatus.PAY;

    @DisplayName("할부개월수, 결제금액, 결제 타입을 입력 받아서 객체를 생성할 수 있다.")
    @Test
    void createTest() {
        assertThat(PayInfo.create(installmentMonths, payAmount, payStatus)).isNotNull();
    }

    @DisplayName("범위를 벗어난 결제 금액으로 객체를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidParams")
    void validationTest(BigDecimal invalidPayAmount) {
        assertThatThrownBy(() -> PayInfo.create(installmentMonths, invalidPayAmount, payStatus))
                .isInstanceOf(InvalidPayAmountException.class);
    }
    public static Stream<BigDecimal> invalidParams() {
        return Stream.of(
                // invalid payAmount
                BigDecimal.valueOf(99),
                BigDecimal.valueOf(1000000001),
                BigDecimal.valueOf(100000000000L)
        );
    }

    @DisplayName("동등성 비교가 가능해야 한다.")
    @Test
    void equalTest() {
        PayInfo payInfo1 = PayInfo.create(installmentMonths, payAmount, payStatus);
        PayInfo payInfo2 = PayInfo.create(installmentMonths, payAmount, payStatus);
        assertThat(payInfo1).isEqualTo(payInfo2);
    }
}
