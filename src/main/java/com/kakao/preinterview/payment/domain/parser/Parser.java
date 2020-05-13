package com.kakao.preinterview.payment.domain.parser;

public class Parser {
    private final int limit;
    private final String value;

    public Parser(int limit, String value) {
        this.limit = limit;
        this.value = value;
    }

    public String defaultNumberParse() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i ++) {
            result.append(" ");
        }
        return result.toString() + value;
    }
}
