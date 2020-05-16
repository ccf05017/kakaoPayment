package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(paymentHistory.getEncryptedCardInfo()).isEqualTo(fakeEncryptedCardInfo.getEncryptedValue());
        assertThat(paymentHistory.getInstallmentMonth()).isEqualTo(fakePayment.getInstallmentMonthFormatMonth());
        assertThat(paymentHistory.getPayAmount()).isEqualTo(fakePayment.getPayAmountString());
        assertThat(paymentHistory.getPaymentStatus()).isEqualTo(payStatus);
        assertThat(paymentHistory.isCanceled()).isEqualTo(isCanceled);
    }
    public static Stream<Arguments> paymentStream() {
        return Stream.of(
                Arguments.of(FakePaymentInfoFactory.createFakePayment(), "PAY", false),
                Arguments.of(FakePaymentInfoFactory.createFakeCancelPayment(), "PAY_CANCEL", true)
        );
    }
}
