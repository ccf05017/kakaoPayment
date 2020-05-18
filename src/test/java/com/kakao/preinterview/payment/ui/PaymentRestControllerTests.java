package com.kakao.preinterview.payment.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.domain.history.FakePaymentHistoryFactory;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.FakePaymentInfoFactory;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.exceptions.*;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(PaymentRestController.class)
class PaymentRestControllerTests {
    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
    }

    @DisplayName("적절한 카드번호, 유효기간, cvc, 할부개월수, 결제금액을 입력해서 결제 시 201")
    @ParameterizedTest
    @MethodSource("validRequestDtos")
    void doPayTaxAutoTest(DoPayRequestDto validRequestDto) throws Exception {
        given(paymentService.doPay(any(DoPayRequestDto.class))).willReturn("XXXXXXXXXXXXXXXXXXXX");

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(validRequestDto))
        )
                    .andExpect(status().isCreated())
                    .andExpect(header().stringValues("Location", "/payments/XXXXXXXXXXXXXXXXXXXX"))
                    .andExpect(jsonPath("$.managementNumber", is("XXXXXXXXXXXXXXXXXXXX")))
        ;
    }
    public static Stream<DoPayRequestDto> validRequestDtos() {
        return Stream.of(
                DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777).installmentMonth(0)
                        .payAmount(BigDecimal.valueOf(110000)).build(),
                DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777).installmentMonth(0)
                        .payAmount(BigDecimal.valueOf(110000)).tax(BigDecimal.valueOf(20)).build()
        );
    }

    @DisplayName("유효하지 않은 카드번호로 결제 신청시 400")
    @ParameterizedTest
    @MethodSource("invalidCardNumbers")
    void doPayFailWithInvalidCardNumbers(
            DoPayRequestDto invalidDoPayRequestDto,
            RuntimeException runtimeException,
            String errorMessage
    ) throws Exception {
        given(paymentService.doPay(any(DoPayRequestDto.class)))
                .willThrow(runtimeException);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidDoPayRequestDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(errorMessage)))
        ;
    }
    public static Stream<Arguments> invalidCardNumbers() {
        return Stream.of(
                // Invalid CardNumber
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234L).duration("1125").cvc(777).installmentMonth(0)
                                .payAmount(BigDecimal.valueOf(110000)).build(),
                        new InvalidCardInfoParamException("CardNumber"),
                        "CardNumber"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234123412341234123L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(110000)).build(),
                        new InvalidCardInfoParamException("CardNumber"),
                        "CardNumber"
                ),

                // Invalid Duration
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("112512").cvc(777)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(110000)).build(),
                        new InvalidCardInfoParamException("Duration"),
                        "Duration"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1").cvc(777)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(110000)).build(),
                        new InvalidCardInfoParamException("Duration"),
                        "Duration"
                ),

                // Invalid Cvc
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(7)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(110000)).build(),
                        new InvalidCardInfoParamException("Cvc"),
                        "Cvc"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(71235)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(110000)).build(),
                        new InvalidCardInfoParamException("Cvc"),
                        "Cvc"
                ),

                // Invalid InstallmentMonth
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(-1).payAmount(BigDecimal.valueOf(110000)).build(),
                        new NotExistInstallmentFormatMonth(),
                        "Invalid Installment Month"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(13).payAmount(BigDecimal.valueOf(110000)).build(),
                        new NotExistInstallmentFormatMonth(),
                        "Invalid Installment Month"
                ),

                // Invalid PayAmount
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(1)).build(),
                        new InvalidPayAmountException(),
                        "Invalid Pay Amount"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(100000000000000L)).build(),
                        new InvalidPayAmountException(),
                        "Invalid Pay Amount"
                ),

                // Invalid TaxAmount
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(BigDecimal.valueOf(100000L))
                                .tax(BigDecimal.valueOf(10000000000L)).build(),
                        new InvalidTaxAmountException(),
                        "Invalid Tax Amount"
                )
        );
    }

    @DisplayName("결제 취소한 적 없는 결제 이력에 대해 세금자동계산 결제전액취소 시도 - 성공(200)")
    @Test
    void paymentCancelAllByAutoTax() throws Exception {
        PaymentHistory fakePaymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        Payment fakeCancelPayment = FakePaymentInfoFactory.createFakeCancelPayment();
        PayCancelRequestDto validCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber(fakePaymentHistory.getManagementNumber())
                .cancelAmount(fakePaymentHistory.getPayAmount())
                .build();
        given(paymentService.cancelAll(validCancelRequestDto))
                .willReturn(fakeCancelPayment);

        mockMvc.perform(put("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(validCancelRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.managementNumber", is(fakeCancelPayment.getManagementNumberValue()))
                    )
        ;
    }

    @DisplayName("결제 취소한 적 없는 결제 이력에 대해 세금수동계산 결제전액취소 시도 - 성공(200)")
    @Test
    void paymentCancelAllByManualTax() throws Exception {
        PaymentHistory fakePaymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        Payment fakeCancelPayment = FakePaymentInfoFactory.createFakeCancelPayment();

        BigDecimal manualTaxValue = BigDecimal.valueOf(1);
        assertThat(manualTaxValue.compareTo(fakePaymentHistory.getTax()) < 0);

        PayCancelRequestDto validCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber(fakePaymentHistory.getManagementNumber())
                .cancelAmount(fakePaymentHistory.getPayAmount())
                .tax(manualTaxValue)
                .build();
        given(paymentService.cancelAll(validCancelRequestDto))
                .willReturn(fakeCancelPayment);

        mockMvc.perform(put("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(validCancelRequestDto)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.managementNumber", is(fakeCancelPayment.getManagementNumberValue()))
                )
        ;
    }

    @DisplayName("결제취소 요청 금액이 제시된 결제 이력의 결제 금액과 일치하지 않는 금액으로 결제전액취소 시도 - 실패(400)")
    @ParameterizedTest
    @ValueSource(longs = {100000, 130000})
    void paymentCancelAllFailWithInvalidCancelAmount(long invalidPayCancelValue) throws Exception {
        PaymentHistory fakePaymentHistory = FakePaymentHistoryFactory.createPaymentHistory();

        BigDecimal invalidPaymentCancelAllAmount = BigDecimal.valueOf(invalidPayCancelValue);
        assertThat(fakePaymentHistory.getPayAmount().compareTo(invalidPaymentCancelAllAmount) != 0 ).isTrue();

        PayCancelRequestDto invalidCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber(fakePaymentHistory.getManagementNumber())
                .cancelAmount(invalidPaymentCancelAllAmount)
                .build();

        given(paymentService.cancelAll(invalidCancelRequestDto)).willThrow(new InvalidPayCancelAmountException());

        mockMvc.perform(put("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidCancelRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Invalid Payment Cancel Amount")));
    }

    @DisplayName("제시된 결제 이력의 부가가치세 금액보다 많은 부가가치세 금액으로 결제전액취소 시도 - 실패(400)")
    @Test
    void paymentCancelAllFailWithInvalidTaxAmount() throws Exception {
        PaymentHistory fakePaymentHistory = FakePaymentHistoryFactory.createPaymentHistory();

        BigDecimal invalidPaymentCancelAllTaxAmount = BigDecimal.valueOf(1000000);
        assertThat(fakePaymentHistory.getTax().compareTo(invalidPaymentCancelAllTaxAmount) < 0 ).isTrue();

        PayCancelRequestDto invalidCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber(fakePaymentHistory.getManagementNumber())
                .cancelAmount(fakePaymentHistory.getPayAmount())
                .tax(invalidPaymentCancelAllTaxAmount)
                .build();

        given(paymentService.cancelAll(invalidCancelRequestDto)).willThrow(new InvalidTaxAmountException());

        mockMvc.perform(put("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidCancelRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Tax Amount")));
    }

    @DisplayName("취소된 적 있는 결제 이력에 대해 결제전액취도 시도 - 실패(400)")
    @Test
    void paymentCancelAllFailWithAlreadyCanceledPaymentHistory() throws Exception {
        PaymentHistory fakePaymentHistory = FakePaymentHistoryFactory.createPaymentHistory();
        fakePaymentHistory.toCanceled();
        assertThat(fakePaymentHistory.isCanceled()).isTrue();

        PayCancelRequestDto invalidCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber(fakePaymentHistory.getManagementNumber())
                .cancelAmount(fakePaymentHistory.getPayAmount())
                .build();

        given(paymentService.cancelAll(invalidCancelRequestDto)).willThrow(new TryCancelFromCanceledPaymentException());

        mockMvc.perform(put("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidCancelRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Already Canceled")));
    }

    @DisplayName("결제 취소 이력에 대해 결제전액취소 시도 - 실패(400)")
    @Test
    void paymentCancelAllFailToPaymentCancelHistory() throws Exception {
        PaymentHistory paymentCancelHistory = FakePaymentHistoryFactory.createPaymentCancelHistory();
        assertThat(paymentCancelHistory.getPaymentTypeName()).isEqualTo("PAY_CANCEL");

        PayCancelRequestDto invalidCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber(paymentCancelHistory.getManagementNumber())
                .cancelAmount(paymentCancelHistory.getPayAmount())
                .build();

        given(paymentService.cancelAll(invalidCancelRequestDto)).willThrow(new TryCancelFromCanceledPaymentException());

        mockMvc.perform(put("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidCancelRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Already Canceled")));
    }

    @DisplayName("결제전액취소 된 결제 이력에 대해 결제부분취소 시도 - 실패(400)")
    @Test
    void paymentCancelPartialFailToCanceledPaymentHistory() throws Exception {
        PayCancelRequestDto invalidPartialCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber("alreadyCanceledPaymentHistory")
                .cancelAmount(BigDecimal.valueOf(3))
                .build();

        given(paymentService.cancelPartial(invalidPartialCancelRequestDto))
                .willThrow(new TryCancelFromCanceledPaymentException());

        mockMvc.perform(patch("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidPartialCancelRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Already Canceled")));
    }

    @DisplayName("결제취소이력(부분취소, 전액취소) 이력에 대해 결제부분취소 시도 - 실패(400)")
    @Test
    void paymentCancelPartialFailToPaymentCancelHistory() throws Exception {
        PayCancelRequestDto invalidPartialCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber("alreadyCanceledPaymentHistory")
                .cancelAmount(BigDecimal.valueOf(3))
                .build();

        given(paymentService.cancelPartial(invalidPartialCancelRequestDto))
                .willThrow(new TryCancelFromCanceledPaymentException());

        mockMvc.perform(patch("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidPartialCancelRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Already Canceled")));
    }

    @DisplayName("잘못된 요청 금액으로 결제부분취소(요청 금액 >= 결제이력 금액, 요청 금액 > 결제이력 잔액) 시도 시 - 실패(400)")
    @Test
    void paymentCancelPartialFailWithTooBigRequestAmountThanOriginal() throws Exception {
        PayCancelRequestDto invalidPartialCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber("alreadyCanceledPaymentHistory")
                .cancelAmount(BigDecimal.valueOf(3))
                .build();

        given(paymentService.cancelPartial(invalidPartialCancelRequestDto))
                .willThrow(new InvalidPayCancelAmountException());

        mockMvc.perform(patch("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidPartialCancelRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Invalid Payment Cancel Amount")));
    }

    @DisplayName("잘못된 세금 계산액으로 결제부분취소 시도 시 - 실패(400)")
    @Test
    void paymentCancelPartialFailWithInvalidTaxAmount() throws Exception {
        PayCancelRequestDto invalidPartialCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber("alreadyCanceledPaymentHistory")
                .cancelAmount(BigDecimal.valueOf(3))
                .build();

        given(paymentService.cancelPartial(invalidPartialCancelRequestDto))
                .willThrow(new InvalidTaxAmountException());

        mockMvc.perform(patch("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidPartialCancelRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Invalid Tax Amount")));
    }

    @DisplayName("적절한 세금 계산액으로 결제부분취소 시도 시 - 성공(200)")
    @Test
    void paymentCancelPartialSuccess() throws Exception {
        PayCancelRequestDto validPartialCancelRequestDto = PayCancelRequestDto.builder()
                .managementNumber("thisTaxAmountIsInvalidAutoManualBoth")
                .cancelAmount(BigDecimal.valueOf(3))
                .build();

        PaymentHistory paymentCancelPartialHistory = FakePaymentHistoryFactory.createPaymentCancelPartialHistory();
        assertThat(paymentCancelPartialHistory.getPaymentTypeName()).isEqualTo("PAY_PARTIAL_CANCEL");
        given(paymentService.cancelPartial(validPartialCancelRequestDto))
                .willReturn(FakePaymentHistoryFactory.createPaymentCancelPartialHistory());

        mockMvc.perform(patch("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(validPartialCancelRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.managementNumber", is("ZZZZZZZZZZZZZZZZZZZZ")));
    }
}
