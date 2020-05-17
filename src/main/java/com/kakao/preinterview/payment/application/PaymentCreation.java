package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;

@FunctionalInterface
public interface PaymentCreation {
    Payment create(DoPayRequestDto resource);
}
