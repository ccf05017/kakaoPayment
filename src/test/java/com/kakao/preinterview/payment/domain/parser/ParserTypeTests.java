package com.kakao.preinterview.payment.domain.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

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
}
