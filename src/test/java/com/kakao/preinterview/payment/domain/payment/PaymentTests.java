package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTests {
    private String cardNumber;
    private String duration;
    private String cvc;
    private String monthlyPayLength;
    private String tax;

    @BeforeEach
    public void setup() {
        cardNumber = "testCardNumber";
        duration = "testDuration";
        cvc = "testCvc";
        monthlyPayLength = "testMonthlyPayLength";
        tax = "testTax";
    }

    @DisplayName("카드번호, 유효기간, cvc, 할부개월수, 결제금액, 부가가치세를 입력받아서 객체 생성")
    @Test
    public void create() {
        Payment paymnet = new Payment(cardNumber, duration, cvc, monthlyPayLength, tax);
        assertThat(paymnet).isNotNull();
    }
}
