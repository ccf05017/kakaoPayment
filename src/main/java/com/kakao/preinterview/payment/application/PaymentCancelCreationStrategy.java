package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.PaymentFactory;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;

public enum PaymentCancelCreationStrategy {
    CANCEL_AUTO_TAX((PaymentHistory paymentHistory, String key, PayCancelRequestDto resource) ->
            PaymentFactory.createPaymentCancelAllByAutoTax(paymentHistory, key, resource.getCancelAmount())),
    CANCEL_MANUAL_TAX((PaymentHistory paymentHistory, String key, PayCancelRequestDto resource) ->
            PaymentFactory.createPaymentCancelAllByManualTax(
                    paymentHistory, key, resource.getCancelAmount(), resource.getTax())
    );

    private PaymentCancelCreation paymentCancelCreation;

    PaymentCancelCreationStrategy(PaymentCancelCreation paymentCancelCreation) {
        this.paymentCancelCreation = paymentCancelCreation;
    }

    public static PaymentCancelCreationStrategy select(PayCancelRequestDto resource) {
        if (resource.getTax() == null) return CANCEL_AUTO_TAX;
        return CANCEL_MANUAL_TAX;
    }

    public Payment create(PaymentHistory paymentHistory, String key, PayCancelRequestDto resource) throws Exception {
        return this.paymentCancelCreation.cancelCreate(paymentHistory, key, resource);
    }
}
