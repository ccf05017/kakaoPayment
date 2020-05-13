package com.kakao.preinterview.payment.domain.parser;

public class Parser {
    private final int limit;
    private final String value;

    public Parser(int limit, String value) {
        this.limit = limit;
        this.value = value;
    }

    public String numberDefaultParse() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i ++) {
            result.append(" ");
        }
        return result.toString() + value;
    }

    public String numberZeroParse() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i ++) {
            result.append("0");
        }
        return result.toString() + value;
    }

    public String numberLeftParse() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i ++) {
            result.append(" ");
        }
        return value + result.toString();
    }
}
