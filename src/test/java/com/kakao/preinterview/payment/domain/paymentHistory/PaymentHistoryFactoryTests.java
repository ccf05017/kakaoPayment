package com.kakao.preinterview.payment.domain.paymentHistory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentHistoryFactoryTests {
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

    @DisplayName("부가가치세 자동계산 결제 객체 생성")
    @Test
    void createPaymentByAutoTax() throws Exception {
        PaymentHistory paymentHistory = PaymentFactory.createPaymentAutoTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key
        );
        assertThat(paymentHistory).isNotNull();
        assertThat(paymentHistory.getTax()).isEqualTo(Tax.autoCreate(payAmount));
    }

    @DisplayName("부가가치세 수동계산 결제 객체 생성")
    @Test
    void createPaymentByManualTax() throws Exception {
        PaymentHistory paymentHistory = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        assertThat(paymentHistory).isNotNull();
        assertThat(paymentHistory.getTax()).isEqualTo(Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount));
    }

    @DisplayName("부가가치세 자동계산 결제전액취소 객체 생성")
    @Test
    void createPaymentCancelAllByAutoTax() throws Exception {
        PaymentHistory paymentHistory = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        PaymentHistory canceledPaymentHistory = PaymentFactory.createPaymentCancelAllByAutoTax(paymentHistory);

        assertThat(canceledPaymentHistory.getRelatedManagementNumber()).isEqualTo(paymentHistory.getManagementNumber());
        assertThat(canceledPaymentHistory.getTax()).isEqualTo(paymentHistory.getTax());
    }

    @DisplayName("부가가치세 수동계산 결제전액취소 객체 생성 - 결제 부가가치세보다 낮은 금액으로 요청 시 성공")
    @Test
    void createPaymentCancelAllByManualTaxSuccess() throws Exception {
        PaymentHistory paymentHistory = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        BigDecimal requestTaxValue = BigDecimal.valueOf(1);
        PaymentHistory canceledPaymentHistory = PaymentFactory.createPaymentCancelAllByManualTax(paymentHistory, requestTaxValue);

        assertThat(canceledPaymentHistory.getRelatedManagementNumber()).isEqualTo(paymentHistory.getManagementNumber());
        assertThat(canceledPaymentHistory.getTax()).isEqualTo(Tax.createManualCancelAllTax(paymentHistory.getTax(), requestTaxValue));
    }

    @DisplayName("부가가치세 수동계산 결제전액취소 객체 생성 - 결제 부가가치세보다 높은 금액으로 요청 시 실패")
    @Test
    void createPaymentCancelAllByManualTaxFail() throws Exception {
        PaymentHistory paymentHistory = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                key,
                taxAmount
        );
        BigDecimal requestTaxValue = BigDecimal.valueOf(100000);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByManualTax(paymentHistory, requestTaxValue))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
