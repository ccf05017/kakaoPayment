package com.kakao.preinterview.payment.domain.payment;

import java.util.Objects;
import java.util.UUID;

public class ManagementNumber {
    private String value;

    protected ManagementNumber(String value) {
        this.value = value;
    }

    public static ManagementNumber create() {
        UUID uuid = UUID.randomUUID();
        return new ManagementNumber(uuid.toString()
                .replaceAll("-", "")
                .substring(0,20));
    }

    protected static boolean checkEqualPossibility(int recursiveTime) {
        ManagementNumber standard = ManagementNumber.create();
        for (int i = 0; i < recursiveTime; i ++) {
            ManagementNumber newMgmt = ManagementNumber.create();
            if (standard.equals(newMgmt)) return true;
        }
        return false;
    }

    public int length() {
        return this.value.length();
    }

    protected String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagementNumber that = (ManagementNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
