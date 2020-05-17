package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private CardCompanyInfoRepository cardCompanyInfoRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @DisplayName("올바른 요청이 왔을 때 결제 진행후 카드사에 데이터 전송하고 기록하기")
    @Test
    void doPaySuccess() throws Exception {
        DoPayRequestDto validDoPayRequestDto = DoPayRequestDto.builder().cardNumber(1234567890123456L)
                .duration("1125").cvc(777).installmentMonth(0).payAmount(110000L).build();

        paymentService.doPay(validDoPayRequestDto);

        verify(cardCompanyInfoRepository).save(any(CardCompanyInfo.class));
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }
}
