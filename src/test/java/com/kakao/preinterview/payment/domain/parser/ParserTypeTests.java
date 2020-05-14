package com.kakao.preinterview.payment.domain.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTypeTests {
    @DisplayName("ParserType으로 숫자 기본정렬(우측 정렬 빈자리 공백) 수행 가능")
    @ParameterizedTest
    @CsvSource(value = { "4:33:'  33'", "4:421:' 421' ", "5:421:'  421'" }, delimiter = ':')
    void numberDefaultParseTest(int limit, String value, String result) {
        String parsed = ParserType.NUMBER_DEFAULT.parse(limit, value);
        assertThat(parsed).isEqualTo(result);
    }
}
