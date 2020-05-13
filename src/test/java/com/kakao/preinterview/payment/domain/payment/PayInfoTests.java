package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PayInfoTests {
    private int installmentMonths = 12;
    private BigDecimal payAmount = BigDecimal.valueOf(1000);
    private PayStatus payStatus = PayStatus.PAY;

    @DisplayName("할부개월수, 결제금액, 결제 타입을 입력 받아서 객체를 생성할 수 있다.")
    @Test
    void createTest() {
        assertThat(PayInfo.create(installmentMonths, payAmount, payStatus)).isNotNull();
    }
}
