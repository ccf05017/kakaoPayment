package com.kakao.preinterview.payment.domain.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTests {
    @DisplayName("숫자 기본정렬 수행(우측 정렬 빈 자리 공백) 확인")
    @ParameterizedTest
    @CsvSource(value = { "4:33:'  33'", "4:421:' 421' ", "5:421:'  421'" }, delimiter = ':')
    void numberDefaultTest(int limit, String value, String result) {
        Parser parser = new Parser(limit, value);
        String parsed = parser.defaultNumberParse();
        assertThat(parsed).isEqualTo(result);
    }
}
