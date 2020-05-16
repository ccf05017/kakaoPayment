package com.kakao.preinterview.payment.domain.payment.exceptions;

public class InvalidCardInfoParamException extends RuntimeException {
    public InvalidCardInfoParamException(String message) {
        super(message);
    }
}
