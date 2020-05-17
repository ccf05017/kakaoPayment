package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;

import java.math.BigDecimal;
import java.util.Objects;

public class Tax {
    private BigDecimal value;

    private Tax(BigDecimal value) {
        this.value = value;
    }

    public static Tax createFromPaymentHistory(PaymentHistory paymentHistory) {
        return new Tax(paymentHistory.getTax());
    }

    public static Tax manualCreate(BigDecimal value, BigDecimal payAmount) {
        manualValidation(value, payAmount);
        return new Tax(value);
    }

    private static void manualValidation(BigDecimal value, BigDecimal payAmount) {
        if (value == null || payAmount == null) throw new InvalidTaxAmountException();
        if (BigDecimal.ZERO.compareTo(value) > 0) throw new InvalidTaxAmountException();
        if (payAmount.compareTo(value) < 0) throw new InvalidTaxAmountException();
    }

    public static Tax autoCreate(BigDecimal payAmount) {
        autoValidation(payAmount);
        BigDecimal autoCreatedValue = payAmount.divide(BigDecimal.valueOf(11), BigDecimal.ROUND_HALF_UP);
        return new Tax(autoCreatedValue);
    }

    private static void autoValidation(BigDecimal payAmount) {
        if (payAmount == null) throw new InvalidTaxAmountException();
        if (BigDecimal.ZERO.compareTo(payAmount) >= 0) throw new InvalidTaxAmountException();
    }

    public static Tax createManualCancelAllTax(Tax originalTax, BigDecimal requestTaxValue) {
        if (originalTax.getValue().compareTo(requestTaxValue) < 0) throw new InvalidTaxAmountException();
        return new Tax(requestTaxValue);
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tax tax = (Tax) o;
        return Objects.equals(value, tax.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
