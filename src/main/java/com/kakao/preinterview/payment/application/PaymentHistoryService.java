package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.ui.dto.GetPayResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentHistoryService {
    @Transactional
    public GetPayResponseDto getPaymentHistory(String managementNumber) {
        return null;
    }
}
