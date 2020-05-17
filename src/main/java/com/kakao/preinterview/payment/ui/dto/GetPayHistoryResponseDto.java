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
public class GetPayHistoryResponseDto {
    private String managementNumber;
    private CardInfoData cardInfoData;
    private String status;
    private BigDecimal payAmount;
    private BigDecimal taxAmount;
}
