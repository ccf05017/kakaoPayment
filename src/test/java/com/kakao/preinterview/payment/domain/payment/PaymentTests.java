package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

class PaymentTests {
    private Integer installmentMonths;
    private BigDecimal payAmount;
    private Long cardNumber;
    private Integer duration;
    private Integer cvc;
    private String key;
    private Long taxAmount;

    @BeforeEach
    public void setup() {
        installmentMonths = 12;
        payAmount = BigDecimal.valueOf(1000);
        cardNumber = 1111222233334444L;
        duration = 1231;
        cvc = 123;
        key = "testKey";
        taxAmount = 150L;
    }
}
