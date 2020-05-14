package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTests {
    private Integer installmentMonths;
    private BigDecimal payAmount;
    private PayStatus payStatus;
    private String encryptedCardInfo;
    private Tax tax;

    @BeforeEach
    public void setup() {
        installmentMonths = 12;
        payAmount = BigDecimal.valueOf(1000);
        payStatus = PayStatus.PAY;
        encryptedCardInfo = "testEncrypted";
    }

    @DisplayName("할부개월수, 결제금액, 결제타입, 암호화 된 카드정보를 입력받아서 객체를 만들 수 있다. (부가가치세 자동 계산")
    @Test
    public void create() {
        Payment payment = new Payment(installmentMonths, payAmount, payStatus, encryptedCardInfo);
        assertThat(payment).isNotNull();
    }
}
