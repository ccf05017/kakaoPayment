package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;
import java.util.Objects;

public class PayInfo {
    private final BigDecimal MIN_PAY_AMOUNT = BigDecimal.valueOf(100);
    private final BigDecimal MAX_PAY_AMOUNT = BigDecimal.valueOf(1000000000);

    private InstallmentMonth installmentMonth;
    private BigDecimal payAmount;
    private PayStatus payStatus;

    private PayInfo(InstallmentMonth installmentMonth, BigDecimal payAmount, PayStatus payStatus) {
        validation(payAmount);

        this.installmentMonth = installmentMonth;
        this.payAmount = payAmount;
        this.payStatus = payStatus;
    }

    private void validation(BigDecimal payAmount) {
        payAmountValidation(payAmount);
    }

    private void payAmountValidation(BigDecimal payAmount) {
        if (MIN_PAY_AMOUNT.compareTo(payAmount) > 0 || MAX_PAY_AMOUNT.compareTo(payAmount) < 0)
            throw new IllegalArgumentException("Invalid PayAmount");
    }

    public static PayInfo create(InstallmentMonth installmentMonths, BigDecimal payAmount, PayStatus payStatus) {
        return new PayInfo(installmentMonths, payAmount, payStatus);
    }

    public InstallmentMonth getInstallmentMonth() {
        return installmentMonth;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public PayStatus getPayStatus() {
        return payStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayInfo payInfo = (PayInfo) o;
        return Objects.equals(installmentMonth, payInfo.installmentMonth) &&
                Objects.equals(payAmount, payInfo.payAmount) &&
                payStatus == payInfo.payStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(installmentMonth, payAmount, payStatus);
    }
}
