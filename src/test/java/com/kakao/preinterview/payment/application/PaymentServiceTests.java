package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayCancelAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.TryCancelFromCanceledPaymentException;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTests {
    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private CardCompanyInfoRepository cardCompanyInfoRepository;
    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private PaymentHistoryService paymentHistoryService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(paymentService, "key", "testKey");
    }

    @DisplayName("올바른 요청이 왔을 때 결제 진행후 카드사에 데이터 전송하고 기록하기")
    @Test
    void doPaySuccess() throws Exception {
        DoPayRequestDto validDoPayRequestDto = DoPayRequestDto.builder().cardNumber(1234567890123456L)
                .duration("1125").cvc(777).installmentMonth(0).payAmount(110000L).build();

        paymentService.doPay(validDoPayRequestDto);

        verify(cardCompanyInfoRepository).save(any(CardCompanyInfo.class));
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @DisplayName("올바른 결제전액취소 요청시 취소 후 결제취소 이력 반환")
    @Test
    void payCancelAllTest() throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber(paymentHistory.getManagementNumber())
                .cancelAmount(paymentHistory.getPayAmount())
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willReturn(Optional.of(paymentHistory));

        Payment paymentCancel = paymentService.cancelAll(resource);

        assertThat(paymentCancel.getTaxValue()).isEqualTo(paymentHistory.getTax());
        assertThat(paymentCancel.getPayTypeName()).isEqualTo("PAY_CANCEL");
        verify(paymentHistoryService).toCancelHistory(paymentHistory.getManagementNumber());
        verify(paymentHistoryRepository).save(any());
        verify(cardCompanyInfoRepository).save(any());
    }

    @DisplayName("존재하지 않는 건에 대해 결제전액취소 요청 시 NotExistPaymentHistoryException")
    @Test
    void payCancelAllFailWithNotExistPaymentHistory() {
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber("notExist")
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willThrow(new NotExistPaymentHistoryException());

        assertThatThrownBy(() -> paymentService.cancelAll(resource))
                .isInstanceOf(NotExistPaymentHistoryException.class);
    }

    @DisplayName("부가가치세 자동 계산으로 결제 이력과 일치하지 않는 금액으로 결제전액취소 요청 시 InvalidPayCancelAmountException")
    @ParameterizedTest
    @ValueSource(longs = {1, 100000000})
    void payCancelAllTaxAutoFailWithInvalidPayCancelAmount(long invalidCancelValue) throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber(paymentHistory.getManagementNumber())
                .cancelAmount(BigDecimal.valueOf(invalidCancelValue))
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willReturn(Optional.of(paymentHistory));

        assertThatThrownBy(() -> paymentService.cancelAll(resource))
                .isInstanceOf(InvalidPayCancelAmountException.class);
    }

    @DisplayName("부가가치세 수동 계산으로 결제 이력과 일치하지 않는 금액으로 결제전액취소 요청 시 InvalidPayCancelAmountException")
    @ParameterizedTest
    @ValueSource(longs = {1, 100000000})
    void payCancelAllTaxManualFailWithInvalidPayCancelAmount(long invalidCancelValue) throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber(paymentHistory.getManagementNumber())
                .cancelAmount(BigDecimal.valueOf(invalidCancelValue))
                .tax(paymentHistory.getTax())
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willReturn(Optional.of(paymentHistory));

        assertThatThrownBy(() -> paymentService.cancelAll(resource))
                .isInstanceOf(InvalidPayCancelAmountException.class);
    }

    @DisplayName("부가가치세 수동 계산 시 결제 이력의 부가가치세보다 높은 금액으로 결제전액취소 요청 시 InvalidTaxAmountException")
    @Test
    void payCancelAllTaxManualFailWithInvalidRequestTaxAmount() throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber(paymentHistory.getManagementNumber())
                .cancelAmount(paymentHistory.getPayAmount())
                .tax(BigDecimal.valueOf(1000000))
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willReturn(Optional.of(paymentHistory));

        assertThatThrownBy(() -> paymentService.cancelAll(resource))
                .isInstanceOf(InvalidTaxAmountException.class);
    }

    @DisplayName("이미 취소한 결제 이력에 결제전액취소 요청 시 TryCancelFromCanceledPaymentException")
    @Test
    void payCancelFailWithCanceledPaymentHistory() throws Exception {
        PaymentHistory paymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        paymentHistory.toCanceled();
        assertThat(paymentHistory.isCanceled()).isTrue();
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber(paymentHistory.getManagementNumber())
                .cancelAmount(paymentHistory.getPayAmount())
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willReturn(Optional.of(paymentHistory));

        assertThatThrownBy(() -> paymentService.cancelAll(resource))
                .isInstanceOf(TryCancelFromCanceledPaymentException.class);
    }

    @DisplayName("결제 취소 이력으로 결제전액취소 요청 시 TryCancelFromCanceledPaymentException")
    @Test
    void payCancelFailWithPaymentCancelHistory() throws Exception {
        PaymentHistory paymentCancelHistory = FakePaymentHistoryFactory.createPaymentCancelHistory();
        assertThat(paymentCancelHistory.getPaymentTypeName()).isEqualTo("PAY_CANCEL");
        PayCancelRequestDto resource = PayCancelRequestDto.builder()
                .managementNumber(paymentCancelHistory.getManagementNumber())
                .cancelAmount(paymentCancelHistory.getPayAmount())
                .build();

        given(paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber()))
                .willReturn(Optional.of(paymentCancelHistory));

        assertThatThrownBy(() -> paymentService.cancelAll(resource))
                .isInstanceOf(TryCancelFromCanceledPaymentException.class);
    }
}
