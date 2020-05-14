package com.kakao.preinterview.payment.domain.parser;

public enum ParserType {
    NUMBER_DEFAULT("nb", (limit, value) -> {
        return emptySpaceGenerate(" ", limit, value) + value;
    });

    private String typeName;
    private DoParse doParse;

    ParserType(String typeName, DoParse doParse) {
        this.typeName = typeName;
        this.doParse = doParse;
    }

    public String parse(int limit, String value) {
        return doParse.doParse(limit, value);
    }

    private static String emptySpaceGenerate(String marker, int limit, String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i++) {
            result.append(marker);
        }
        return result.toString();
    }
}
