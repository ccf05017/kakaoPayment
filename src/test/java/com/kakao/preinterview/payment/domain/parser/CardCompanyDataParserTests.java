package com.kakao.preinterview.payment.domain.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CardCompanyDataParserTests {
    @DisplayName("limit, data, ParseType을 입력 받아서 카드사 전송용 데이터로 파싱 가능")
    @ParameterizedTest
    @MethodSource("parseData")
    void cardCompanyDataParserDefaultTest(int limit, String data, String parseType , String result) {
        String parsed = CardCompanyDataParser.parse(limit, data, parseType);
        assertThat(parsed).isEqualTo(result);
    }
    public static Stream<Arguments> parseData() {
        return Stream.of(
                Arguments.of(4, "446", "nd", " 446"),
                Arguments.of(10, "PAYMENT", "sl", "PAYMENT   "),
                Arguments.of(20, "1234567890123456", "nl", "1234567890123456    "),
                Arguments.of(2, "00", "nd", "00"),
                Arguments.of(4, "1125", "nl", "1125"),
                Arguments.of(3, "777", "nl", "777"),
                Arguments.of(10, "110000", "nd", "    110000"),
                Arguments.of(10, "10000", "nr", "0000010000"),
                Arguments.of(20, "XXXXXXXXXXXXXXXXXXXX", "sl", "XXXXXXXXXXXXXXXXXXXX"),
                Arguments.of(
                        300,
                        "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" +
                                "YYYYYYYYYYYYYYYYYYYYYYYY",
                        "sl",
                        "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" +
                                "YYYYYYYYYYYYYYYYYYYYYYYY                                            " +
                                "                                                                    " +
                                "                                                                    " +
                                "                    "
                ),
                Arguments.of(47, "", "sl", "                                               ")
        );
    }
}
