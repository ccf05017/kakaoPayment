package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

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
                EncryptedCardInfo.create(cardInfo, key),
                tax
        );
    }

    @DisplayName("카드사 전송용 카드 번호 확인")
    @Test
    void cardNumberForCardCompanyTest() {
        assertThat(fakePayment.getCardNumberForCardCompany()).isEqualTo("1234567890123456    ");
    }

    @DisplayName("카드사 전송용 할부개월 수 확인")
    @Test
    void installmentMonthsForCardCompanyTest() {
        assertThat(fakePayment.getInstallmentMonthForCardCompany()).isEqualTo("00");
    }

    @DisplayName("카드사 전송용 카드유효기간 확인")
    @Test
    void durationForCardCompanyTest() {
        assertThat(fakePayment.getDurationForCardCompany()).isEqualTo("1125");
    }
}
