package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class FakePaymentInfoFactory {
    public static Payment createFakePayment() {
        ManagementNumber managementNumber = new ManagementNumber("XXXXXXXXXXXXXXXXXXXX");
        InstallmentMonth installmentMonths = InstallmentMonth.LUMPSUM;
        BigDecimal payAmount = BigDecimal.valueOf(110000);
        PayStatus payStatus = PayStatus.PAY;
        PayInfo payInfo = PayInfo.create(installmentMonths, payAmount, payStatus);
        Long cardNumber = 1234567890123456L;
        String duration = "1125";
        Integer cvc = 777;
        CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);
        Tax tax = Tax.manualCreate(BigDecimal.valueOf(10000), payAmount);

        Payment fakePayment = new Payment(
                managementNumber,
                null,
                payInfo,
                cardInfo,
                tax
        );

        return fakePayment;
    }

    public static Payment createFakeCancelPayment() {
        ManagementNumber managementNumber = new ManagementNumber("ZZZZZZZZZZZZZZZZZZZZ");
        ManagementNumber relatedManagementNumber = new ManagementNumber("XXXXXXXXXXXXXXXXXXXX");
        InstallmentMonth installmentMonths = InstallmentMonth.LUMPSUM;
        BigDecimal payAmount = BigDecimal.valueOf(110000);
        PayStatus payStatus = PayStatus.PAY_CANCEL;
        PayInfo payInfo = PayInfo.create(installmentMonths, payAmount, payStatus);
        Long cardNumber = 1234567890123456L;
        String duration = "1125";
        Integer cvc = 777;
        CardInfo cardInfo = CardInfo.create(cardNumber, duration, cvc);
        Tax tax = Tax.manualCreate(BigDecimal.valueOf(10000), payAmount);

        Payment fakePayment = new Payment(
                managementNumber,
                relatedManagementNumber,
                payInfo,
                cardInfo,
                tax
        );

        return fakePayment;
    }
}
