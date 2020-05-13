package com.kakao.preinterview.payment.domain.parser;

public class Parser {
    private final int limit;
    private final String value;

    public Parser(int limit, String value) {
        this.limit = limit;
        this.value = value;
    }

    public String numberDefaultParse() {
        return emptySpaceGenerate(" ") + value;
    }

    public String numberZeroParse() {
        return emptySpaceGenerate("0") + value;
    }

    public String numberLeftParse() {
        return value + emptySpaceGenerate(" ");
    }

    public String stringLeftParse() {
        return value + emptySpaceGenerate(" ");
    }

    private String emptySpaceGenerate(String marker) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i++) {
            result.append(marker);
        }
        return result.toString();
    }
}
