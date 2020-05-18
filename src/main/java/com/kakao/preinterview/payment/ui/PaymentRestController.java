package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PaymentRestController {
    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<PayResponseDto> doPayment(
            @Valid @RequestBody DoPayRequestDto resource
    ) throws Exception {
        PaymentHistory paymentHistory = paymentService.doPay(resource);
        String url = "/payments/" + paymentHistory.getManagementNumber();

        return ResponseEntity
                .created(new URI(url))
                .body(PayResponseDto.builder()
                        .managementNumber(paymentHistory.getManagementNumber())
                        .build());
    }

    @PutMapping("/payments")
    public PayResponseDto payCancelAll(@Valid @RequestBody PayCancelRequestDto resource) throws Exception {
        Payment paymentCancelAll = paymentService.cancelAll(resource);

        return PayResponseDto.builder()
                .managementNumber(paymentCancelAll.getManagementNumberValue())
                .build();
    }

    @PatchMapping("/payments")
    public PayResponseDto payCancelPartial(@Valid @RequestBody PayCancelRequestDto resource) throws Exception {
        PaymentHistory paymentCancelPartialHistory = paymentService.cancelPartial(resource);

        return PayResponseDto.builder()
                .managementNumber(paymentCancelPartialHistory.getManagementNumber())
                .build();
    }
}
