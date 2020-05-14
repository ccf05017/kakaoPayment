package com.kakao.preinterview.payment.domain.parser;

@FunctionalInterface
public interface Parser {
    String parse(int limit, String value);
}
