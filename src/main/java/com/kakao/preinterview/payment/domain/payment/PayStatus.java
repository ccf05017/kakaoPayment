package com.kakao.preinterview.payment.domain.payment;

public enum PayStatus {
    PAY("PAYMENT"), PAY_CANCEL("CANCEL"), PAY_PARTIAL_CANCEL("CANCEL");

    private String name;

    PayStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
