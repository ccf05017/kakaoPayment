package com.kakao.preinterview.payment.domain.parser;

import com.kakao.preinterview.payment.domain.parser.exceptions.NotExistParserTypeNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParserTypeTests {
    @DisplayName("숫자 기본정렬 수행(우측 정렬 빈 자리 공백) 확인")
    @ParameterizedTest
    @CsvSource(value = { "4:33:'  33'", "4:421:' 421' ", "5:421:'  421'" }, delimiter = ':')
    void numberDefaultParseTest(int limit, String value, String result) {
        String parsed = ParserType.NUMBER_DEFAULT.parse(limit, value);
        assertThat(parsed).isEqualTo(result);
    }

    @DisplayName("숫자 우측 정렬 0 파싱 수행(우측 정렬 빈자리 0) 확인")
    @ParameterizedTest
    @CsvSource(value = { "4:33:'0033'", "4:421:'0421' ", "5:421:'00421'" }, delimiter = ':')
    void numberZeroTest(int limit, String value, String result) {
        String parsed = ParserType.NUMBER_RIGHT.parse(limit, value);
        assertThat(parsed).isEqualTo(result);
    }

    @DisplayName("숫자 좌측 정렬 파싱 수행(좌측정렬 빈자리 공백) 확인")
    @ParameterizedTest
    @CsvSource(value = { "4:33:'33  '", "4:421:'421 ' ", "5:421:'421  '" }, delimiter = ':')
    void numberLeftTest(int limit, String value, String result) {
        String parsed = ParserType.NUMBER_LEFT.parse(limit, value);
        assertThat(parsed).isEqualTo(result);
    }

    @DisplayName("문자열 좌측 정렬 파싱 수행(좌측정렬 빈자리 공백) 확인")
    @ParameterizedTest
    @CsvSource(value = { "4:aa:'aa  '", "4:aaa:'aaa ' ", "5:aaa:'aaa  '" }, delimiter = ':')
    void StringLeftTest(int limit, String value, String result) {
        String parsed = ParserType.STRING_LEFT.parse(limit, value);
        assertThat(parsed).isEqualTo(result);
    }

    @DisplayName("typeName 입력 시 알맞는 ParserType 객체 반환")
    @ParameterizedTest
    @MethodSource("typeGenerator")
    void parseTypeGenerateTest(String typeName, ParserType result) {
        ParserType parserType = ParserType.create(typeName);
        assertThat(parserType).isEqualTo(result);
    }
    public static Stream<Arguments> typeGenerator() {
        return Stream.of(
                Arguments.of("nd", ParserType.NUMBER_DEFAULT),
                Arguments.of("nr", ParserType.NUMBER_RIGHT),
                Arguments.of("nl", ParserType.NUMBER_LEFT),
                Arguments.of("sl", ParserType.STRING_LEFT)
        );
    }

    @DisplayName("typeName에 정의되지 않은 내용 입력 시 IllegalArgumentException")
    @Test
    void parseTypeGenerateFailWithNotRegisteredTypeName() {
        assertThatThrownBy(() -> ParserType.create("hello")).isInstanceOf(NotExistParserTypeNameException.class);
    }
}
