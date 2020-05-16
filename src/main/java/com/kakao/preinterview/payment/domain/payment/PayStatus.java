package com.kakao.preinterview.payment.domain.payment;

public enum PayStatus {
    PAY("PAY", "PAYMENT"),
    PAY_CANCEL("PAY_CANCEL", "CANCEL"),
    PAY_PARTIAL_CANCEL("PAY_PARTIAL_CANCEL","CANCEL");

    private String name;
    private String cardCompanyName;

    PayStatus(String name, String cardCompanyName) {
        this.name = name;
        this.cardCompanyName = cardCompanyName;
    }

    public String getName() {
        return name;
    }

    public String getCardCompanyName() {
        return cardCompanyName;
    }
}
