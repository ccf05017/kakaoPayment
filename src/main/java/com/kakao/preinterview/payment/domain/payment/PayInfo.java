package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class PayInfo {
    private final int MIN_INSTALLMENT_MONTHS = 0;
    private final int MAX_INSTALLMENT_MONTHS = 12;
    private final BigDecimal MIN_PAY_AMOUNT = BigDecimal.valueOf(100);
    private final BigDecimal MAX_PAY_AMOUNT = BigDecimal.valueOf(1000000000);

    private Integer installmentMonths;
    private BigDecimal payAmount;
    private PayStatus payStatus;

    private PayInfo(int installmentMonths, BigDecimal payAmount, PayStatus payStatus) {
        validation(installmentMonths, payAmount, payStatus);

        this.installmentMonths = installmentMonths;
        this.payAmount = payAmount;
        this.payStatus = payStatus;
    }

    private void validation(int installmentMonths, BigDecimal payAmount, PayStatus payStatus) {
        installmentMonthsValidation(installmentMonths);
        payAmountValidation(payAmount);
    }

    private void installmentMonthsValidation(int installmentMonths) {
        if (installmentMonths < MIN_INSTALLMENT_MONTHS || installmentMonths > MAX_INSTALLMENT_MONTHS)
            throw new IllegalArgumentException("Invalid InstallmentMonths");
    }

    private void payAmountValidation(BigDecimal payAmount) {
        if (MIN_PAY_AMOUNT.compareTo(payAmount) > 0 || MAX_PAY_AMOUNT.compareTo(payAmount) < 0)
            throw new IllegalArgumentException("Invalid PayAmount");
    }

    public static PayInfo create(int installmentMonths, BigDecimal payAmount, PayStatus payStatus) {
        return new PayInfo(installmentMonths, payAmount, payStatus);
    }
}
