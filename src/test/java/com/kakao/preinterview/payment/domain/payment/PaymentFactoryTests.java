package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentFactoryTests {
    private InstallmentMonth installmentMonths;
    private BigDecimal payAmount;
    private Long cardNumber;
    private String duration;
    private Integer cvc;
    private Long taxAmount;

    @BeforeEach
    public void setup() {
        installmentMonths = InstallmentMonth.TWELVE;
        payAmount = BigDecimal.valueOf(1000);
        cardNumber = 1111222233334444L;
        duration = "1231";
        cvc = 123;
        taxAmount = 150L;
    }

    @DisplayName("부가가치세 자동계산 결제 객체 생성")
    @Test
    void createPaymentByAutoTax() {
        Payment payment = PaymentFactory.createPaymentAutoTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.autoCreate(payAmount));
    }

    @DisplayName("부가가치세 수동계산 결제 객체 생성")
    @Test
    void createPaymentByManualTax() {
        Payment payment = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                taxAmount
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.manualCreate(BigDecimal.valueOf(taxAmount), payAmount));
    }

    @DisplayName("결제 History를 기반으로 부가가치세 자동계산 결제전액취소 진행 - 성공")
    @Test
    void createPaymentCancelAllByAutoTaxFromNotCanceledPaymentHistory() throws Exception {
        Payment payment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);

        Payment canceledPayment = PaymentFactory.createPaymentCancelAllByAutoTax(paymentHistory, "testKey");

        assertThat(canceledPayment.getRelatedManagementNumber()).isEqualTo(payment.getManagementNumber());
        assertThat(canceledPayment.getTax()).isEqualTo(payment.getTax());
    }

    @DisplayName("취소된 결제 History를 기반으로 부가가치세 자동계산 결제전액취소 진행 - 실패")
    @Test
    void createPaymentCancelAllByAutoTaxFromCanceledPaymentHistory() {

    }

    @DisplayName("결제전액취소 History를 기반으로 부가가치세 자동계산 결제전액취소 진행 - 실패")
    @Test
    void createPaymentCancelAllByAutoTaxFromPaymentCancelHistory() {

    }

    @DisplayName("부가가치세 수동계산 결제전액취소 객체 생성 - 결제 부가가치세보다 낮은 금액으로 요청 시 성공")
    @Test
    void createPaymentCancelAllByManualTaxSuccess() {
        Payment payment = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                taxAmount
        );
        BigDecimal requestTaxValue = BigDecimal.valueOf(1);
        Payment canceledPayment = PaymentFactory.createPaymentCancelAllByManualTax(payment, requestTaxValue);

        assertThat(canceledPayment.getRelatedManagementNumber()).isEqualTo(payment.getManagementNumber());
        assertThat(canceledPayment.getTax()).isEqualTo(Tax.createManualCancelAllTax(payment.getTax(), requestTaxValue));
    }

    @DisplayName("부가가치세 수동계산 결제전액취소 객체 생성 - 결제 부가가치세보다 높은 금액으로 요청 시 실패")
    @Test
    void createPaymentCancelAllByManualTaxFail() {
        Payment payment = PaymentFactory.createPaymentManualTax(
                installmentMonths,
                payAmount,
                PayStatus.PAY,
                cardNumber,
                duration,
                cvc,
                taxAmount
        );
        BigDecimal requestTaxValue = BigDecimal.valueOf(100000);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByManualTax(payment, requestTaxValue))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
