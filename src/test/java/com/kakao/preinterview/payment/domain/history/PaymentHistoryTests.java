package com.kakao.preinterview.payment.domain.history;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.exceptions.PaymentCancelCannotUpdateException;
import com.kakao.preinterview.payment.domain.payment.FakePaymentInfoFactory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentHistoryTests {
    private Payment fakePayment;
    private EncryptedCardInfo fakeEncryptedCardInfo;

    @BeforeEach
    public void setup() throws Exception {
        fakePayment = FakePaymentInfoFactory.createFakePayment();
        fakeEncryptedCardInfo = EncryptedCardInfo.create(fakePayment.getCardInfo(), "testKey");
    }

    @DisplayName("Payment, EncryptedCardInfo 객체를 받아서 결제 히스토리 객체 생성 가능")
    @ParameterizedTest
    @MethodSource("paymentStream")
    void createTest(Payment payment, String payStatus , boolean isCanceled) {
        PaymentHistory paymentHistory = new PaymentHistory(payment, fakeEncryptedCardInfo);

        assertThat(paymentHistory).isNotNull();
        assertThat(paymentHistory.getManagementNumber()).isEqualTo(payment.getManagementNumberValue());
        assertThat(paymentHistory.getRelatedManagementNumber()).isEqualTo(payment.getRelatedManagementNumberValue());
        assertThat(paymentHistory.getEncryptedCardInfo()).isEqualTo(fakeEncryptedCardInfo.getEncryptedValue());
        assertThat(paymentHistory.getInstallmentMonthFormatMonth()).isEqualTo(payment.getInstallmentMonthFormatMonth());
        assertThat(paymentHistory.getPayAmount()).isEqualTo(payment.getPayAmountString());
        assertThat(paymentHistory.getPaymentTypeName()).isEqualTo(payStatus);
        assertThat(paymentHistory.isCanceled()).isEqualTo(isCanceled);
    }
    public static Stream<Arguments> paymentStream() {
        return Stream.of(
                Arguments.of(FakePaymentInfoFactory.createFakePayment(), "PAY", false),
                Arguments.of(FakePaymentInfoFactory.createFakeCancelPayment(), "PAY_CANCEL", true)
        );
    }

    @DisplayName("취소 여부가 false인 PaymentHistory 객체의 취소 여부 상태를 바꿀 수 있다.")
    @Test
    void changeCanceledTest() {
        PaymentHistory paymentHistory = new PaymentHistory(fakePayment, fakeEncryptedCardInfo);
        assertThat(paymentHistory.isCanceled()).isEqualTo(false);

        paymentHistory.toCanceled();
        assertThat(paymentHistory.isCanceled()).isEqualTo(true);
    }

    @DisplayName("결제 건에 대한 revision 증가 시도 시 성공")
    @Test
    void revisionUpTest() {
        PaymentHistory paymentHistory = new PaymentHistory(
                FakePaymentInfoFactory.createFakePayment(), fakeEncryptedCardInfo
        );
        assertThat(paymentHistory.getRevision()).isEqualTo(0);

        paymentHistory.revisionUp();
        assertThat(paymentHistory.getRevision()).isEqualTo(1);
    }

    @DisplayName("결제 취소 건에 대한 revision 증가 시도 시 PaymentCancelCannotUpdateException 발생")
    @Test
    void revisionUpFailTest() {
        PaymentHistory paymentHistory = new PaymentHistory(
                FakePaymentInfoFactory.createFakeCancelPayment(), fakeEncryptedCardInfo
        );
        assertThat(paymentHistory.getRevision()).isEqualTo(0);

        assertThatThrownBy(paymentHistory::revisionUp).isInstanceOf(PaymentCancelCannotUpdateException.class);
    }
}
