package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.payment.exceptions.NotExistInstallmentFormatMonth;

import java.util.Arrays;
import java.util.Optional;

public enum InstallmentMonth {
    LUMPSUM(0, "00"),
    TWO(2, "02"),
    THREE(3, "03"),
    FOUR(4, "04"),
    FIVE(5, "05"),
    SIX(6, "06"),
    SEVEN(7, "07"),
    EIGHT(8, "08"),
    NINE(9, "09"),
    TEN(10, "10"),
    ELEVEN(11, "11"),
    TWELVE(12, "12")
    ;

    private int month;
    private String formatMonth;

    InstallmentMonth(int month, String formatMonth) {
        this.month = month;
        this.formatMonth = formatMonth;
    }

    public static InstallmentMonth createFromFormatMonth(String formatMonth) {
        Optional<InstallmentMonth> result = Arrays.asList(InstallmentMonth.values())
                .stream().filter(installmentMonth -> formatMonth.equals(installmentMonth.getFormatMonth()))
                .findFirst();
        return result.orElseThrow(NotExistInstallmentFormatMonth::new);
    }

    public int getMonth() {
        return month;
    }

    public String getFormatMonth() {
        return formatMonth;
    }
}
