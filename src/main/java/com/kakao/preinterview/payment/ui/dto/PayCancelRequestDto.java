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
@NoArgsConstructor
@AllArgsConstructor
public class PayCancelRequestDto {
    @NotNull
    @NotEmpty
    private String managementNumber;
    @NotNull
    private BigDecimal cancelAmount;
    private BigDecimal tax;
}
