package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayCancelAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.TryCancelFromCanceledPaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentFactoryTests {
    private InstallmentMonth installmentMonths;
    private BigDecimal payAmount;
    private Long cardNumber;
    private String duration;
    private Integer cvc;
    private BigDecimal taxAmount;

    @BeforeEach
    public void setup() {
        installmentMonths = InstallmentMonth.TWELVE;
        payAmount = BigDecimal.valueOf(1000);
        cardNumber = 1111222233334444L;
        duration = "1231";
        cvc = 123;
        taxAmount = BigDecimal.valueOf(150);
    }

    @DisplayName("부가가치세 자동계산 결제 객체 생성")
    @Test
    void createPaymentByAutoTax() {
        Payment payment = PaymentFactory.createPaymentAutoTax(
                installmentMonths,
                payAmount,
                PayType.PAY,
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
                PayType.PAY,
                cardNumber,
                duration,
                cvc,
                taxAmount
        );
        assertThat(payment).isNotNull();
        assertThat(payment.getTax()).isEqualTo(Tax.manualCreate(taxAmount, payAmount));
    }

    @DisplayName("결제 History를 기반으로 부가가치세 자동계산 결제전액취소 진행 - 성공")
    @Test
    void createPaymentCancelAllByAutoTaxFromNotCanceledPaymentHistory() throws Exception {
        Payment payment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);

        Payment canceledPayment = PaymentFactory.createPaymentCancelAllByAutoTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount());

        assertThat(canceledPayment.getRelatedManagementNumberValue()).isEqualTo(paymentHistory.getManagementNumber());
        assertThat(canceledPayment.getTaxValue()).isEqualTo(paymentHistory.getTax());
    }

    @DisplayName("취소된 결제 History를 기반으로 부가가치세 자동계산 결제전액취소 진행 - 실패")
    @Test
    void createPaymentCancelAllByAutoTaxFromCanceledPaymentHistory() throws Exception {
        Payment payment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);
        paymentHistory.toCanceled();

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByAutoTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount())
        )
                .isInstanceOf(TryCancelFromCanceledPaymentException.class);
    }

    @DisplayName("결제전액취소 History를 기반으로 부가가치세 자동계산 결제전액취소 진행 - 실패")
    @Test
    void createPaymentCancelAllByAutoTaxFromPaymentCancelHistory() throws Exception {
        Payment canceledPayment = FakePaymentInfoFactory.createFakeCancelPayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(canceledPayment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(canceledPayment, encryptedCardInfo);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByAutoTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount())
        )
                .isInstanceOf(TryCancelFromCanceledPaymentException.class);
    }

    @DisplayName("결제 History 금액과 일치하지 않는 금액으로 부가가치세 자동계산 결제전액취소 진행 - 실패")
    @ParameterizedTest
    @ValueSource(longs = {1, 150000})
    void createPaymentCancelAllByAutoTaxWithInvalidCancelAmount(long invalidValue) throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        BigDecimal invalidCancelAmount = BigDecimal.valueOf(invalidValue);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByAutoTax(
                paymentHistory, "testKey", invalidCancelAmount)
        ).isInstanceOf(InvalidPayCancelAmountException.class);
    }

    @DisplayName("결제 History를 기반으로 적절한 값의 부가가치세 수동계산 결제전액취소 시도 - 성공")
    @Test
    void createPaymentCancelAllByManualTaxSuccess() throws Exception {
        Payment payment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);
        BigDecimal requestTaxValue = BigDecimal.valueOf(1);

        Payment canceledPayment = PaymentFactory.createPaymentCancelAllByManualTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount(), requestTaxValue
        );

        assertThat(canceledPayment.getRelatedManagementNumberValue()).isEqualTo(paymentHistory.getManagementNumber());
        assertThat(canceledPayment.getTax().getValue()).isEqualTo(requestTaxValue);
    }

    @DisplayName("취소된 결제 History를 기반으로 적절한 값의 부가가치세 수동계산 결제전액취소 시도 - 실패")
    @Test
    void createPaymentCancelAllByManualTaxFromCanceledPaymentHistory() throws Exception {
        Payment payment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);
        paymentHistory.toCanceled();
        BigDecimal requestTaxValue = BigDecimal.valueOf(1);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByManualTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount(), requestTaxValue
        )).isInstanceOf(TryCancelFromCanceledPaymentException.class);
    }

    @DisplayName("결제전액취소 History를 기반으로 적절한 값의 부가가치세 수동계산 결제전액취소 시도 - 실패")
    @Test
    void createPaymentCancelAllByManualTaxFromPaymentCancelHistory() throws Exception {
        Payment canceledPayment = FakePaymentInfoFactory.createFakeCancelPayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(canceledPayment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(canceledPayment, encryptedCardInfo);
        BigDecimal requestTaxValue = BigDecimal.valueOf(1);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByManualTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount(), requestTaxValue
        )).isInstanceOf(TryCancelFromCanceledPaymentException.class);
    }

    @DisplayName("결제 History를 기반으로 부적절한 값의 부가가치세 수동계산 결제전액취소 시도 - 실패")
    @Test
    void createPaymentCancelAllByManualTaxFail() throws Exception {
        Payment payment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);
        BigDecimal invalidTaxRequestValue = BigDecimal.valueOf(100000);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByManualTax(
                paymentHistory, "testKey", paymentHistory.getPayAmount(), invalidTaxRequestValue
        )).isInstanceOf(InvalidTaxAmountException.class);
    }

    @DisplayName("결제 History 금액과 일치하지 않는 금액으로 부가가치세 수동계산 결제전액취소 진행 - 실패")
    @ParameterizedTest
    @ValueSource(longs = {1, 150000})
    void createPaymentCancelAllByManualTaxWithInvalidCancelAmount(long invalidValue) throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        BigDecimal invalidCancelAmount = BigDecimal.valueOf(invalidValue);
        BigDecimal requestTaxValue = BigDecimal.valueOf(100000);

        assertThatThrownBy(() -> PaymentFactory.createPaymentCancelAllByManualTax(
                paymentHistory, "testKey", invalidCancelAmount, requestTaxValue)
        ).isInstanceOf(InvalidPayCancelAmountException.class);
    }
}
