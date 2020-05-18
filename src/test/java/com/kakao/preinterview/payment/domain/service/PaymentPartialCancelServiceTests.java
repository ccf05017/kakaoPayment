package com.kakao.preinterview.payment.domain.service;

import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.Tax;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayCancelAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentPartialCancelServiceTests {
    private PaymentPartialCancelService paymentPartialCancelService;

    @BeforeEach
    public void setup() {
        paymentPartialCancelService = new PaymentPartialCancelService();
    }

    @DisplayName("최종취소 여부를 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"10:true", "2:false", "11:false"}, delimiter = ':')
    void isFinalPartialCancel(int requestInt, boolean result) {
        BigDecimal remainSum = BigDecimal.valueOf(10);
        BigDecimal requestValue = BigDecimal.valueOf(requestInt);

        assertThat(paymentPartialCancelService.isFinalPartialCancel(requestValue, remainSum)).isEqualTo(result);
    }

    @DisplayName("결제부분취소 요청 금액이 결제이력 금액보다 크거나 같은 경우 InvalidPayCancelAmountException(공통 검증)")
    @ParameterizedTest
    @ValueSource(longs = {110000L, 120000L})
    void paymentHistoryPayAmountValidationTest(long longValue) throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        BigDecimal requestValue = BigDecimal.valueOf(longValue);

        assertThatThrownBy(() ->
                paymentPartialCancelService.paymentHistoryPayAmountValidation(
                        requestValue, paymentHistory.getPayAmount())).isInstanceOf(InvalidPayCancelAmountException.class);
    }

    @DisplayName("결제부분취소 요청 금액이 결제이력 잔액보다 큰 경우 InvalidPayCancelAmountException(공통 검증)")
    @Test
    void paymentHistoryPayRemainValidation() throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PaymentHistory paymentCancelPartialHistory = FakePaymentHistoryFactory.createPaymentCancelPartialHistory();
        BigDecimal remains = paymentHistory.getPayAmount().subtract(paymentCancelPartialHistory.getPayAmount());
        BigDecimal requestValue = BigDecimal.valueOf(100000);
        assertThat(requestValue.compareTo(remains) > 0).isTrue();

        assertThatThrownBy(() -> paymentPartialCancelService.paymentHistoryPayRemainValidation(requestValue, remains))
                .isInstanceOf(InvalidPayCancelAmountException.class);
    }

    @DisplayName("부가가치세 자동계산으로 최종 취소를 진행 중 " +
            "자동계산 된 부가가치세가 결제이력 부가가치세 잔액보다 적은 경우 InvalidTaxAmountException")
    @Test
    void partialCancelAutoTaxFailTest() throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto invalidPartialCancelRequestDto = PayCancelRequestDto.builder()
                .cancelAmount(BigDecimal.valueOf(1100))
                .build();
        BigDecimal amountRemainSum = BigDecimal.valueOf(1100);
        BigDecimal taxRemainSum = BigDecimal.valueOf(1000);

        assertThatThrownBy(() -> paymentPartialCancelService.doByAutoTax(
                paymentHistory, "testKey", invalidPartialCancelRequestDto, amountRemainSum, taxRemainSum))
                .isInstanceOf(InvalidTaxAmountException.class);
    }

    @DisplayName("부가가치세 자동계산으로 최종 취소를 진행 중 " +
            "자동계산 된 부가가치세가 결제이력 부가가치세 잔액보다 큰 경우 결제이력 부가가치세 잔액으로 취소가 진행된다.")
    @Test
    void partialCancelAutoTaxSuccessWithBiggerAutoTax() throws Exception {
        BigDecimal cancelRequestAmount = BigDecimal.valueOf(11000);
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto validPartialCancelRequestDto = PayCancelRequestDto.builder()
                .cancelAmount(cancelRequestAmount)
                .build();
        BigDecimal amountRemainSum = BigDecimal.valueOf(11000);
        BigDecimal taxRemainSum = BigDecimal.valueOf(100);

        assertThat(Tax.autoCreate(cancelRequestAmount).getValue().compareTo(taxRemainSum) > 0).isTrue();

        Payment partialCancelPayment = paymentPartialCancelService.doByAutoTax(
                paymentHistory, "testKey", validPartialCancelRequestDto, amountRemainSum, taxRemainSum);
        assertThat(partialCancelPayment.getPayTypeName()).isEqualTo("PAY_PARTIAL_CANCEL");
        assertThat(partialCancelPayment.getRelatedManagementNumberValue()).isEqualTo("XXXXXXXXXXXXXXXXXXXX");
        assertThat(partialCancelPayment.getTaxValue()).isEqualTo(taxRemainSum);
    }

    @DisplayName("부가가치세 자동계산으로 최종 취소를 진행 중 " +
            "자동계산 된 부가가치세가 결제이력 부가가치세 잔액과 같은 경우 자동계산된 부가가치세 금액으로 취소가 진행된다.")
    @Test
    void partialCancelAutoTaxSuccessWithSameAutoTax() throws Exception {
        BigDecimal cancelRequestAmount = BigDecimal.valueOf(11000);
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto validPartialCancelRequestDto = PayCancelRequestDto.builder()
                .cancelAmount(cancelRequestAmount)
                .build();
        BigDecimal amountRemainSum = BigDecimal.valueOf(11000);
        BigDecimal taxRemainSum = BigDecimal.valueOf(1000);

        assertThat(Tax.autoCreate(cancelRequestAmount).getValue().compareTo(taxRemainSum) == 0).isTrue();

        Payment partialCancelPayment = paymentPartialCancelService.doByAutoTax(
                paymentHistory, "testKey", validPartialCancelRequestDto, amountRemainSum, taxRemainSum);
        assertThat(partialCancelPayment.getPayTypeName()).isEqualTo("PAY_PARTIAL_CANCEL");
        assertThat(partialCancelPayment.getRelatedManagementNumberValue()).isEqualTo("XXXXXXXXXXXXXXXXXXXX");
        assertThat(partialCancelPayment.getTaxValue()).isEqualTo(taxRemainSum);
    }
}
