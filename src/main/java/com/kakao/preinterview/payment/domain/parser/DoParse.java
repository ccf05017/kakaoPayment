package com.kakao.preinterview.payment.domain.parser;

@FunctionalInterface
public interface DoParse {
    String doParse(int limit, String value);
}
