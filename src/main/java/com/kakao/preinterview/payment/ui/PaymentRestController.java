package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.DoPayResponseDto;
import com.kakao.preinterview.payment.ui.dto.GetPayResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PaymentRestController {
    private final PaymentService paymentService;

    @GetMapping("/payments/{managementNumber}")
    public GetPayResponseDto getPayment(@PathVariable String managementNumber) {
        return paymentService.getPaymentHistory(managementNumber);
    }

    @PostMapping("/payments")
    public ResponseEntity<DoPayResponseDto> doPayment(
            @Valid @RequestBody DoPayRequestDto resource
    ) throws Exception {
        String managementNumber = paymentService.doPay(resource);
        String url = "/payments/" + managementNumber;

        return ResponseEntity
                .created(new URI(url))
                .body(DoPayResponseDto.builder()
                        .managementNumber(managementNumber)
                        .build());
    }
}
