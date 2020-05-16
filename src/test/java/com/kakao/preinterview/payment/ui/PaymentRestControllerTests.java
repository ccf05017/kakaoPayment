package com.kakao.preinterview.payment.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.preinterview.payment.application.PaymentService;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentRestControllerTests {
    @InjectMocks
    private PaymentRestController paymentRestController;

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(paymentRestController).alwaysDo(print()).build();
    }

    @DisplayName("적절한 카드번호, 유효기간, cvc, 할부개월수, 결제금액을 입력해서 결제 요청")
    @Test
    void doPayTaxAutoTest() throws Exception {
        DoPayRequestDto validPayRequestDto = DoPayRequestDto.builder()
                .cardNumber("1234567890123456")
                .duration("1125")
                .cvc(777)
                .installmentMonth(0)
                .payAmount(110000L)
                .build();

        given(paymentService.doPay(any(DoPayRequestDto.class))).willReturn("XXXXXXXXXXXXXXXXXXXX");

        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(validPayRequestDto))
        )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.managementNumber", is("XXXXXXXXXXXXXXXXXXXX")))
        ;
    }
}
