package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.application.PaymentHistoryService;
import com.kakao.preinterview.payment.ui.dto.GetPayHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentHistoryRestController {
    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/paymentHistories/{managementNumber}")
    public GetPayHistoryResponseDto getPayment(@PathVariable String managementNumber) throws Exception {
        return paymentHistoryService.getPaymentHistory(managementNumber);
    }
}
