package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;

@FunctionalInterface
public interface PaymentCancelCreation {
    Payment cancelCreate(PaymentHistory paymentHistory, String key, PayCancelRequestDto resource) throws Exception;
}
