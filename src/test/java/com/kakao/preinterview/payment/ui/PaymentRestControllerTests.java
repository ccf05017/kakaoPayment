package com.kakao.preinterview.payment.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidCardInfoParamException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidPayAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidTaxAmountException;
import com.kakao.preinterview.payment.domain.payment.exceptions.NotExistInstallmentFormatMonth;
import com.kakao.preinterview.payment.ui.dto.CardInfoData;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.GetPayResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                        .payAmount(110000L).build(),
                DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777).installmentMonth(0)
                        .payAmount(110000L).tax(20L).build()
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
                                .payAmount(110000L).build(),
                        new InvalidCardInfoParamException("CardNumber"),
                        "CardNumber"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234123412341234123L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(110000L).build(),
                        new InvalidCardInfoParamException("CardNumber"),
                        "CardNumber"
                ),

                // Invalid Duration
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("112512").cvc(777)
                                .installmentMonth(0).payAmount(110000L).build(),
                        new InvalidCardInfoParamException("Duration"),
                        "Duration"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1").cvc(777)
                                .installmentMonth(0).payAmount(110000L).build(),
                        new InvalidCardInfoParamException("Duration"),
                        "Duration"
                ),

                // Invalid Cvc
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(7)
                                .installmentMonth(0).payAmount(110000L).build(),
                        new InvalidCardInfoParamException("Cvc"),
                        "Cvc"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(71235)
                                .installmentMonth(0).payAmount(110000L).build(),
                        new InvalidCardInfoParamException("Cvc"),
                        "Cvc"
                ),

                // Invalid InstallmentMonth
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(-1).payAmount(110000L).build(),
                        new NotExistInstallmentFormatMonth(),
                        "Invalid Installment Month"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(13).payAmount(110000L).build(),
                        new NotExistInstallmentFormatMonth(),
                        "Invalid Installment Month"
                ),

                // Invalid PayAmount
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(1L).build(),
                        new InvalidPayAmountException(),
                        "Invalid Pay Amount"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(100000000000000L).build(),
                        new InvalidPayAmountException(),
                        "Invalid Pay Amount"
                ),

                // Invalid TaxAmount
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber(1234567890123456L).duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(100000L).tax(10000000000L).build(),
                        new InvalidTaxAmountException(),
                        "Invalid Tax Amount"
                )
        );
    }

    @DisplayName("존재하는 관리번호로 결제내역 조회 시 성공(200)")
    @Test
    void getPaymentTest() throws Exception {
        String managementNumber = "exist";
        GetPayResponseDto fakeGetPayResponseDto = GetPayResponseDto.builder()
                .managementNumber("exist")
                .cardInfoData(CardInfoData.builder()
                        .cardNumber("123456*******456")
                        .duration("1125")
                        .cvc(777)
                        .build())
                .canceled(false)
                .payAmount(BigDecimal.valueOf(110000))
                .taxAmount(BigDecimal.valueOf(10000))
                .build();
        given(paymentService.getPaymentHistory(managementNumber)).willReturn(fakeGetPayResponseDto);

        mockMvc.perform(get("/payments/" + managementNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managementNumber", is("exist")))
                .andExpect(jsonPath("$.cardInfoData.cardNumber", is("123456*******456")))
                .andExpect(jsonPath("$.cardInfoData.duration", is("1125")))
                .andExpect(jsonPath("$.cardInfoData.cvc", is(777)))
                .andExpect(jsonPath("$.canceled", is(false)))
                .andExpect(jsonPath("$.payAmount", is(110000)))
                .andExpect(jsonPath("$.taxAmount", is(10000)))
        ;
    }

    @DisplayName("존재하지 않는 결제 내역 조회 시 실패(404)")
    @Test
    void getPaymentFail() throws Exception {
        String managementNumber = "notExist";
        given(paymentService.getPaymentHistory(managementNumber)).willThrow(new NotExistPaymentHistoryException());

        mockMvc.perform(get("/payments/" + managementNumber))
                .andExpect(status().isNotFound());
    }
}
