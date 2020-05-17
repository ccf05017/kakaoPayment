package com.kakao.preinterview.payment.ui;

import com.kakao.preinterview.payment.application.PaymentHistoryService;
import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.ui.dto.CardInfoData;
import com.kakao.preinterview.payment.ui.dto.GetPayHistoryResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(PaymentHistoryRestController.class)
class PaymentHistoryRestControllerTests {
    @MockBean
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("존재하는 관리번호로 결제내역 조회 시 성공(200)")
    @Test
    void getPaymentTest() throws Exception {
        String managementNumber = "exist";
        GetPayHistoryResponseDto fakeGetPayHistoryResponseDto = GetPayHistoryResponseDto.builder()
                .managementNumber("exist")
                .cardInfoData(CardInfoData.builder()
                        .cardNumber("123456*******456")
                        .duration("1125")
                        .cvc(777)
                        .build())
                .type("PAY")
                .payAmount(BigDecimal.valueOf(110000))
                .taxAmount(BigDecimal.valueOf(10000))
                .build();
        given(paymentHistoryService.getPaymentHistory(managementNumber)).willReturn(fakeGetPayHistoryResponseDto);

        mockMvc.perform(get("/paymentHistories/" + managementNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managementNumber", is("exist")))
                .andExpect(jsonPath("$.cardInfoData.cardNumber", is("123456*******456")))
                .andExpect(jsonPath("$.cardInfoData.duration", is("1125")))
                .andExpect(jsonPath("$.cardInfoData.cvc", is(777)))
                .andExpect(jsonPath("$.type", is("PAY")))
                .andExpect(jsonPath("$.payAmount", is(110000)))
                .andExpect(jsonPath("$.taxAmount", is(10000)))
        ;
    }

    @DisplayName("존재하지 않는 결제 내역 조회 시 실패(404)")
    @Test
    void getPaymentFail() throws Exception {
        String managementNumber = "notExist";
        given(paymentHistoryService.getPaymentHistory(managementNumber))
                .willThrow(new NotExistPaymentHistoryException());

        mockMvc.perform(get("/paymentHistories/" + managementNumber))
                .andExpect(status().isNotFound());
    }
}
