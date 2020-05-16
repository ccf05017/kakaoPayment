package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.payment.exceptions.ParseToCardCompanyDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTests {
    private ManagementNumber managementNumber = new ManagementNumber("XXXXXXXXXXXXXXXXXXXX");
    private InstallmentMonth installmentMonths = InstallmentMonth.LUMPSUM;
    private BigDecimal payAmount = BigDecimal.valueOf(110000);
    private PayStatus payStatus = PayStatus.PAY;
    private PayInfo payInfo = PayInfo.create(installmentMonths, payAmount, payStatus);
    private Long cardNumber = 1234567890123456L;
    private String duration = "1125";
    private Integer cvc = 777;
    private CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);
    private Tax tax = Tax.manualCreate(BigDecimal.valueOf(10000), payAmount);

    private String paymentSampleStringData = "_446PAYMENT___XXXXXXXXXXXXXXXXXXXX1234567890123456____001125777____1100000000010000____________________YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________".replaceAll("_", " ");

    private String key = "testKey";
    private Payment fakePayment;

    @BeforeEach
    void setup() throws Exception {
        fakePayment = new Payment(
                null,
                managementNumber,
                null,
                payInfo,
                cardInfo,
                new EncryptedCardInfo("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"),
                tax
        );
    }

    @DisplayName("Payment 샘플 데이터 확인")
    @Test
    void sample() {
        assertThat(paymentSampleStringData.length())
                .isEqualTo(450);
    }

    @DisplayName("현재 Payment 객체의 상태를 카드사 전송용 형태로 바꿀 수 있어야 함")
    @Test
    void parseToCardCompanyTest() {
        String result = fakePayment.parseToCardCompanyData();
        assertThat(result).isEqualTo(paymentSampleStringData);
    }

    @DisplayName("현재 Payment 객체 상태를 전송용 데이터로 변경했는데 길이가 안 맞을 경우 error")
    @Test
    void parseToCardCompanyValidationTest() {
        assertThatThrownBy(() -> fakePayment.cardCompanyDataValidate("hello"))
                .isInstanceOf(ParseToCardCompanyDataException.class);
    }
}
