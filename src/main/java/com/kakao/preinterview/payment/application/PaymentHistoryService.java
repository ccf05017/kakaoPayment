package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.CardInfo;
import com.kakao.preinterview.payment.domain.service.DecryptService;
import com.kakao.preinterview.payment.ui.dto.GetPayHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final DecryptService decryptService;

    @Transactional
    public GetPayHistoryResponseDto getPaymentHistory(String managementNumber) throws Exception {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByManagementNumber(managementNumber)
                .orElseThrow(NotExistPaymentHistoryException::new);
        CardInfo cardInfo = decryptService.getCardInfoFromPaymentHistory(paymentHistory);

        return GetPayHistoryResponseDto.create(paymentHistory, cardInfo);
    }
}
