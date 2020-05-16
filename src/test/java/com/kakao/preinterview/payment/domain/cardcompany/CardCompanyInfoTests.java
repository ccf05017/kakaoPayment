package com.kakao.preinterview.payment.domain.cardcompany;

import com.kakao.preinterview.payment.domain.cardcompany.exceptions.ParseToCardCompanyDataException;
import com.kakao.preinterview.payment.domain.payment.FakePaymentInfoFactory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CardCompanyInfoTests {
    private Payment fakePayment;
    private String paymentSampleStringData = "_446PAYMENT___XXXXXXXXXXXXXXXXXXXX1234567890123456____001125777____1100000000010000____________________YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________".replaceAll("_", " ");

    @BeforeEach
    public void setup() {
        fakePayment = FakePaymentInfoFactory.createFakePayment();
    }

    @DisplayName("현재 Payment 객체의 상태를 카드사 전송용 형태로 바꿀 수 있어야 함")
    @Test
    void parseToCardCompanyTest() {
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(fakePayment);
        assertThat(cardCompanyInfo.getStringData()).isEqualTo(paymentSampleStringData);
    }

    @DisplayName("현재 Payment 객체 상태를 전송용 데이터로 변경했는데 길이가 안 맞을 경우 error")
    @Test
    void parseToCardCompanyValidationTest() {
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(fakePayment);
        assertThatThrownBy(() -> cardCompanyInfo.validateStringDataLength("hello"))
                .isInstanceOf(ParseToCardCompanyDataException.class);
    }
}
