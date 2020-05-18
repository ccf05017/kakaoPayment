package com.kakao.preinterview.payment.domain.parser.exceptions;

public class CardCompanyDataParsingException extends RuntimeException {
    public CardCompanyDataParsingException(String message) {
        super(message);
    }
}
