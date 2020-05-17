package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.ui.dto.GetPayHistoryResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTests {
    @InjectMocks
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(paymentHistoryService, "key", "testKey");
    }

    @DisplayName("존재하는 결제 이력을 조회해서 GetPayHistoryResponseDto로 변환 성공")
    @Test
    void getPaymentHistory() throws Exception {
        String managementNumber = "XXXXXXXXXXXXXXXXXXXX";
        given(paymentHistoryRepository.findByManagementNumber(managementNumber))
                .willReturn(FakePaymentHistoryFactory.create());

        GetPayHistoryResponseDto paymentHistoryDto = paymentHistoryService.getPaymentHistory(managementNumber);
        assertThat(paymentHistoryDto.getManagementNumber()).isEqualTo(managementNumber);
        assertThat(paymentHistoryDto.getStatus()).isEqualTo("PAY");
        assertThat(paymentHistoryDto.getCardInfoData().getCardNumber()).isEqualTo("123456*******456");
        assertThat(paymentHistoryDto.getPayAmount()).isEqualTo(BigDecimal.valueOf(110000));
        assertThat(paymentHistoryDto.getTaxAmount()).isEqualTo(BigDecimal.valueOf(10000));
    }
}
