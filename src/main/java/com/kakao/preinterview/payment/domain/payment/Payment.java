package com.kakao.preinterview.payment.domain.payment;

public class Payment {
    private String cardNumber;
    private String duration;
    private String cvc;
    private String monthlyPayLength;
    private String tax;

    public Payment(String cardNumber, String duration, String cvc, String monthlyPayLength, String tax) {
        this.cardNumber = cardNumber;
        this.duration = duration;
        this.cvc = cvc;
        this.monthlyPayLength = monthlyPayLength;
        this.tax = tax;
    }
}
