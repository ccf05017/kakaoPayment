package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayAmountException;

import java.math.BigDecimal;
import java.util.Objects;

public class PayInfo {
    private final BigDecimal MIN_PAY_AMOUNT = BigDecimal.valueOf(100);
    private final BigDecimal MAX_PAY_AMOUNT = BigDecimal.valueOf(1000000000);

    private InstallmentMonth installmentMonth;
    private BigDecimal payAmount;
    private PayType payType;

    private PayInfo(InstallmentMonth installmentMonth, BigDecimal payAmount, PayType payType) {
        validation(payAmount);

        this.installmentMonth = installmentMonth;
        this.payAmount = payAmount;
        this.payType = payType;
    }

    private void validation(BigDecimal payAmount) {
        payAmountValidation(payAmount);
    }

    private void payAmountValidation(BigDecimal payAmount) {
        if (MIN_PAY_AMOUNT.compareTo(payAmount) > 0 || MAX_PAY_AMOUNT.compareTo(payAmount) < 0)
            throw new InvalidPayAmountException();
    }

    public static PayInfo create(InstallmentMonth installmentMonths, BigDecimal payAmount, PayType payType) {
        return new PayInfo(installmentMonths, payAmount, payType);
    }

    public InstallmentMonth getInstallmentMonth() {
        return installmentMonth;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public PayType getPayType() {
        return payType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayInfo payInfo = (PayInfo) o;
        return Objects.equals(installmentMonth, payInfo.installmentMonth) &&
                Objects.equals(payAmount, payInfo.payAmount) &&
                payType == payInfo.payType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(installmentMonth, payAmount, payType);
    }
}
