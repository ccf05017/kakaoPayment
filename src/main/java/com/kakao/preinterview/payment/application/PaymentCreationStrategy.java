package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.payment.InstallmentMonth;
import com.kakao.preinterview.payment.domain.payment.PayType;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.PaymentFactory;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;

import java.math.BigDecimal;

public enum PaymentCreationStrategy {
    PAY_AUTO_TAX((DoPayRequestDto resource) -> PaymentFactory.createPaymentAutoTax(
            InstallmentMonth.createFromMonth(resource.getInstallmentMonth()),
            BigDecimal.valueOf(resource.getPayAmount()),
            PayType.PAY,
            resource.getCardNumber(),
            resource.getDuration(),
            resource.getCvc()
    )),
    PAY_MANUAL_TAX((DoPayRequestDto resource) -> PaymentFactory.createPaymentManualTax(
            InstallmentMonth.createFromMonth(resource.getInstallmentMonth()),
            BigDecimal.valueOf(resource.getPayAmount()),
            PayType.PAY,
            resource.getCardNumber(),
            resource.getDuration(),
            resource.getCvc(),
            resource.getTax()
    ));

    private PaymentCreation paymentCreation;

    PaymentCreationStrategy(PaymentCreation paymentCreation) {
        this.paymentCreation = paymentCreation;
    }

    public static PaymentCreationStrategy select(DoPayRequestDto resource) {
        if (resource.getTax() == null) return PAY_AUTO_TAX;
        return PAY_MANUAL_TAX;
    }

    public Payment create(DoPayRequestDto resource) {
        return this.paymentCreation.create(resource);
    }
}
