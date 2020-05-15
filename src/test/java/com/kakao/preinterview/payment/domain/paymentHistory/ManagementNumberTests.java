package com.kakao.preinterview.payment.domain.paymentHistory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ManagementNumberTests {
    @DisplayName("관리번호는 20자리의 랜덤한 유일값으로 생성되야 한다.")
    @Test
    void createManagementNumber() {
        ManagementNumber managementNumber = ManagementNumber.create();
        assertThat(managementNumber.length()).isEqualTo(20);
    }

    @DisplayName("관리번호 중복이 얼마나 일어나는지 확인")
    @ParameterizedTest
    @ValueSource(longs = { 99999999L, 999999999L, 99999999999L, 999999999999L, 9999999999999L, 99999999999999L })
    void equalPossibilityTest() {
        boolean result = ManagementNumber.checkEqualPossibility(1000);
        assertThat(result).isFalse();
    }
}
