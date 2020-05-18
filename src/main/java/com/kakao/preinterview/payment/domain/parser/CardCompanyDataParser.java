package com.kakao.preinterview.payment.domain.parser;

import com.kakao.preinterview.payment.domain.parser.exceptions.CardCompanyDataParsingException;

public class CardCompanyDataParser {
    public static String parse(int limit, String data, String parseType) {
        ParserType parserType = ParserType.create(parseType);
        String parsedString = parserType.parse(limit, data);
        validateParseDataLength(parsedString, limit);
        return parserType.parse(limit, data);
    }

    private static void validateParseDataLength(String parsedString, int limit) {
        if (parsedString.length() > limit) new CardCompanyDataParsingException(parsedString);
    }
}
