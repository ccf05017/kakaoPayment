package com.kakao.preinterview.payment.domain.parser;

public class CardCompanyDataParser {
    public static String parse(int limit, String data, String parseType) {
        ParserType parserType = ParserType.create(parseType);
        return parserType.parse(limit, data);
    }
}
