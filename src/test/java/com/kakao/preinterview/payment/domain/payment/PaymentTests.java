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
    private Long cardNumber;
    private Integer duration;
    private Integer cvc;
    private String key;
    private Long taxAmount;

    @BeforeEach
    public void setup() {
        installmentMonths = 12;
        payAmount = BigDecimal.valueOf(1000);
        payStatus = PayStatus.PAY;
        cardNumber = 1111222233334444L;
        duration = 1231;
        cvc = 123;
        key = "testKey";
        taxAmount = 150L;
    }

    @DisplayName("할부개월수, 결제금액, 결제타입, 암호화 된 카드정보를 입력받아서 객체를 만들 수 있다. (부가가치세 자동 계산")
    @Test
    void createPaymentByAutoTax() throws Exception {
        Payment payment = new Payment(
                installmentMonths,
                payAmount,
                payStatus,
                cardNumber,
                duration,
                cvc,
                key
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.autoCreate(payAmount));
    }
    
    @DisplayName("할부개월수, 결제금액, 결제타입, 암호화 된 카드정보, 부가가치세를 입력받아서 객체를 만들 수 있다. (부가가치세 수동 계산")
    @Test
    void createPaymentByManualTax() throws Exception {
        Payment payment = new Payment(
                installmentMonths,
                payAmount,
                payStatus,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount));
    }
}
