package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.DoPayResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class PaymentRestController {
    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<DoPayResponseDto> doPayment(
            @Valid @RequestBody DoPayRequestDto resource
    ) throws URISyntaxException {
        String managementNumber = paymentService.doPay(resource);
        String url = "/payments/" + managementNumber;

        return ResponseEntity
                .created(new URI(url))
                .body(DoPayResponseDto.builder()
                        .managementNumber(managementNumber)
                        .build());
    }
}
