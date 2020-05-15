package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("세금 자동계산 결제 객체 생성")
    @Test
    void createPaymentByAutoTax() throws Exception {
        Payment payment = Payment.createPaymentAutoTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.autoCreate(payAmount));
    }

    @DisplayName("세금 수동계산 결제 객체 생성")
    @Test
    void createPaymentByManualTax() throws Exception {
        Payment payment = Payment.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount));
    }

    @DisplayName("세금 자동계산 전액 결제취소 객체 생성")
    @Test
    void createPaymentCancelAllByAutoTax() throws Exception {
        Payment payment = Payment.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        Payment canceledPayment = Payment.createPaymentCancelAllByAutoTax(payment);

        assertThat(canceledPayment.getRelatedManagementNumber()).isEqualTo(payment.getManagementNumber());
        assertThat(canceledPayment.getTax()).isEqualTo(payment.getTax());
    }
}
