package com.kakao.preinterview.payment.domain.payment;

import java.util.UUID;

public class ManagementNumber {
    public static String create() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString()
                .replaceAll("-", "")
                .substring(0,20);
    }

    protected static boolean checkEqualPossibility(int recursiveTime) {
        String standard = ManagementNumber.create();
        for (int i = 0; i < recursiveTime; i ++) {
            String newMgmt = ManagementNumber.create();
            if (standard.equals(newMgmt)) return true;
        }
        return false;
    }
}
