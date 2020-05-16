package com.kakao.preinterview.payment.domain.payment;

import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

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
}
