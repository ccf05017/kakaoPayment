package com.kakao.preinterview.payment.domain.service;

import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.CardInfo;
import com.kakao.preinterview.payment.domain.payment.FakePaymentInfoFactory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DecryptServiceTests {
    @InjectMocks
    private DecryptService decryptService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(decryptService, "key", "testKey");
    }

    @DisplayName("결제 이력 정보로부터 복호화 된 카드 정보 획득")
    @Test
    void getCardInfoFromPaymentHistory() throws Exception {
        Payment fakePayment = FakePaymentInfoFactory.createFakePayment();
        CardInfo standard = fakePayment.getCardInfo();
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();

        CardInfo cardInfo = decryptService.getCardInfoFromPaymentHistory(paymentHistory);
        assertThat(cardInfo.getCardNumber()).isEqualTo(standard.getCardNumber());
        assertThat(cardInfo.getCvc()).isEqualTo(standard.getCvc());
        assertThat(cardInfo.getDuration()).isEqualTo(standard.getDuration());
    }
}
