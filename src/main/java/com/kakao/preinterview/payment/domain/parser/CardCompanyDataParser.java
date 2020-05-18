package com.kakao.preinterview.payment.domain.parser;

import com.kakao.preinterview.payment.domain.parser.exceptions.CardCompanyDataParsingException;

public class CardCompanyDataParser {
    public static String parse(int limit, String data, String parseType) {
        validateParseDataLength(data, limit);
        ParserType parserType = ParserType.create(parseType);
        return parserType.parse(limit, data);
    }

    private static void validateParseDataLength(String data, int limit) {
        if (data.length() > limit) new CardCompanyDataParsingException(data);
    }
}
