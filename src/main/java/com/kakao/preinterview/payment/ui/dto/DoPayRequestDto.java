package com.kakao.preinterview.payment.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoPayRequestDto {
    @NotNull
    private Long cardNumber;
    @NotEmpty
    @NotNull
    private String duration;
    @NotNull
    private Integer cvc;
    @NotNull
    private Integer installmentMonth;
    @NotNull
    private BigDecimal payAmount;
    private BigDecimal tax;
}
