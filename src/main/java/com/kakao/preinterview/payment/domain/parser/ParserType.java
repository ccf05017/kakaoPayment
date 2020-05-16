package com.kakao.preinterview.payment.domain.parser;

import java.util.Arrays;
import java.util.Optional;

public enum ParserType {
    NUMBER_DEFAULT("nd", (limit, value) -> {
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
    });

    private String typeName;
    private Parser parser;

    ParserType(String typeName, Parser parser) {
        this.typeName = typeName;
        this.parser = parser;
    }

    public static ParserType create(String typeName) {
        Optional<ParserType> filteredType = Arrays.asList(ParserType.values()).stream()
                .filter(parserType -> typeName.equals(parserType.getTypeName()))
                .findFirst();
        return filteredType.orElseThrow(IllegalArgumentException::new);
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

    public String getTypeName() {
        return typeName;
    }

    public Parser getParser() {
        return parser;
    }
}
