package com.kakao.preinterview.payment.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidCardInfoParamException;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
                DoPayRequestDto.builder().cardNumber("1234567890123456").duration("1125").cvc(777).installmentMonth(0)
                        .payAmount(110000L).build(),
                DoPayRequestDto.builder().cardNumber("1234567890123456").duration("1125").cvc(777).installmentMonth(0)
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
                        DoPayRequestDto.builder().cardNumber("1234").duration("1125").cvc(777).installmentMonth(0)
                                .payAmount(110000L).build(),
                        new InvalidCardInfoParamException("CardNumber"),
                        "CardNumber"
                ),
                Arguments.of(
                        DoPayRequestDto.builder().cardNumber("12341234123412341234").duration("1125").cvc(777)
                                .installmentMonth(0).payAmount(110000L).build(),
                        new InvalidCardInfoParamException("CardNumber"),
                        "CardNumber"
                )
        );
    }
}
