package com.kakao.preinterview.payment.domain.parser;

public enum ParserType {
    NUMBER_DEFAULT("nb", (limit, value) -> {
        return emptySpaceGenerate(" ", limit, value) + value;
    }),
    NUMBER_RIGHT("nr", (limit, value) -> {
        return emptySpaceGenerate("0", limit, value) + value;
    }),
    NUMBER_LEFT("nl", (limit, value) -> {
        return value + emptySpaceGenerate(" ", limit, value);
    }),
    STRING_LEFT("sl", (limit, value) -> {
        return value + emptySpaceGenerate(" ", limit, value);
    }),
    ;


    private String typeName;
    private Parser parser;

    ParserType(String typeName, Parser parser) {
        this.typeName = typeName;
        this.parser = parser;
    }

    public String parse(int limit, String value) {
        return parser.parse(limit, value);
    }

    private static String emptySpaceGenerate(String marker, int limit, String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < limit - value.length(); i++) {
            result.append(marker);
        }
        return result.toString();
    }
}