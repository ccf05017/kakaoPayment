package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CardInfoTests {
    private Long validCardNumber = 12345678901L;
    private String validDuration = "0124";
    private Integer validCvc = 123;

    @DisplayName("카드번호, 유효기간, cvc를 입력 받아서 객체를 생성할 수 있다.")
    @Test
    void createTest() {
        CardInfo cardInfo = CardInfo.create(validCardNumber, validDuration, validCvc);
        assertThat(cardInfo).isNotNull();
    }

    @DisplayName("카드번호, 유효기간, cvc 중 하나라도 잘못되면 객체를 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidParams")
    void createValidationTest(Long cardNumber, String duration, Integer cvc) {
        assertThatThrownBy(()-> CardInfo.create(cardNumber, duration, cvc))
                .isInstanceOf(IllegalArgumentException.class);
    }
    public static Stream<Arguments> invalidParams() {
        return Stream.of(
                // invalid cardNumber
                Arguments.of(123L, "1224", 123),
                Arguments.of(123456789123456789L, "1224", 123),
                // invalid duration
                Arguments.of(12345678901L, "12345", 123),
                Arguments.of(12345678901L, "12", 123),
                // invalid cvc
                Arguments.of(12345678901L, "1224", 12),
                Arguments.of(12345678901L, "1224", 1234)
        );
    }

    @DisplayName("복호화 된 CardInfoFormat 문자열에서 CardInfo를 뽑아낼 수 있어야 한다.")
    @Test
    void decryptTest() {
        CardInfo cardInfo = CardInfo
                .createFromDecryptedRawString(validCardNumber + "|" + validDuration + "|" + validCvc);
        assertThat(cardInfo.getCardNumber()).isEqualTo(validCardNumber);
        assertThat(cardInfo.getDuration()).isEqualTo(validDuration);
        assertThat(cardInfo.getCvc()).isEqualTo(validCvc);
    }

    @DisplayName("동등성 비교가 가능해야 한다.")
    @Test
    void equalTest() {
        CardInfo cardInfo1 = CardInfo.create(validCardNumber, validDuration, validCvc);
        CardInfo cardInfo2 = CardInfo.create(validCardNumber, validDuration, validCvc);

        assertThat(cardInfo1).isEqualTo(cardInfo2);
    }
}
