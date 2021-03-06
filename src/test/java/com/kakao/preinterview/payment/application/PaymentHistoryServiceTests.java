package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.history.exceptions.PaymentCancelCannotUpdateException;
import com.kakao.preinterview.payment.domain.payment.FakePaymentInfoFactory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.service.DecryptService;
import com.kakao.preinterview.payment.ui.dto.GetPayHistoryResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTests {
    @InjectMocks
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private DecryptService decryptService;

    @DisplayName("존재하는 결제 이력을 조회해서 GetPayHistoryResponseDto로 변환 성공")
    @Test
    void getPaymentHistory() throws Exception {
        String managementNumber = "XXXXXXXXXXXXXXXXXXXX";
        PaymentHistory fakePaymentHistory = Optional.of(FakePaymentHistoryFactory.createPaymentHistory()).get();
        Payment fakePayment = FakePaymentInfoFactory.createFakePayment();
        given(paymentHistoryRepository.findByManagementNumber(managementNumber))
                .willReturn(Optional.of(fakePaymentHistory));
        given(decryptService.getCardInfoFromPaymentHistory(fakePaymentHistory)).willReturn(fakePayment.getCardInfo());

        GetPayHistoryResponseDto paymentHistoryDto = paymentHistoryService.getPaymentHistory(managementNumber);
        assertThat(paymentHistoryDto.getManagementNumber()).isEqualTo(managementNumber);
        assertThat(paymentHistoryDto.getType()).isEqualTo("PAY");
        assertThat(paymentHistoryDto.getCardInfoData().getCardNumber()).isEqualTo("123456*******456");
        assertThat(paymentHistoryDto.getPayAmount()).isEqualTo(BigDecimal.valueOf(110000));
        assertThat(paymentHistoryDto.getTaxAmount()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @DisplayName("존재하지 않는 결제 이력 조회 시 NotExistPaymentHistoryException 발생")
    @Test
    void getPaymentHistoryFailWithNotExistManagementNumber() throws Exception {
        String managementNumber = "notExist";
        given(paymentHistoryRepository.findByManagementNumber(managementNumber))
                .willThrow(new NotExistPaymentHistoryException());

        assertThatThrownBy(() -> paymentHistoryService.getPaymentHistory(managementNumber))
                .isInstanceOf(NotExistPaymentHistoryException.class);
    }

    @DisplayName("존재하는 결제 이력에 대한 revision 업데이트 수행 - 성공")
    @Test
    void updateRevision() throws Exception {
        String managementNumber = "exist";
        PaymentHistory fakePaymentHistory = Optional.of(FakePaymentHistoryFactory.createPaymentHistory()).get();
        given(paymentHistoryRepository.findByManagementNumber(managementNumber))
                .willReturn(Optional.of(fakePaymentHistory));

        PaymentHistory paymentHistory = paymentHistoryService.toCancelHistory(managementNumber);
        assertThat(paymentHistory.getRevision()).isEqualTo(1L);
    }

    @DisplayName("존재하지 않는 결제 이력에 대한 revision 업데이트 수행 - 실패")
    @Test
    void updateRevisionFailWithNotExistPaymentHistory() {
        String managementNumber = "notExist";
        given(paymentHistoryRepository.findByManagementNumber(managementNumber))
                .willThrow(new NotExistPaymentHistoryException());

        assertThatThrownBy(() -> paymentHistoryService.toCancelHistory(managementNumber))
                .isInstanceOf(NotExistPaymentHistoryException.class);
    }

    @DisplayName("존재하는 결체취소 이력에 대한 revision 업데이트 수행 - 실패")
    @Test
    void updateRevisionFailWithPaymentCancelHistory() throws Exception {
        String managementNumber = "cancel";
        PaymentHistory fakePaymentCancelHistory = FakePaymentHistoryFactory.createPaymentCancelHistory();
        given(paymentHistoryRepository.findByManagementNumber(managementNumber))
                .willReturn(Optional.of(fakePaymentCancelHistory));

        assertThatThrownBy(() -> paymentHistoryService.toCancelHistory(managementNumber))
                .isInstanceOf(PaymentCancelCannotUpdateException.class);
    }
}
