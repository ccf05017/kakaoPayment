package com.kakao.preinterview.payment.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPayResponseDto {
    private String managementNumber;
    private CardInfoData cardInfoData;
    private boolean canceled;
    private BigDecimal payAmount;
    private BigDecimal taxAmount;
}
