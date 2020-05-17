package com.kakao.preinterview.payment.ui.dto;

import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.CardInfo;
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
    private String type;
    private BigDecimal payAmount;
    private BigDecimal taxAmount;

    public static GetPayHistoryResponseDto create(PaymentHistory paymentHistory, CardInfo cardInfo) {
        return GetPayHistoryResponseDto.builder()
                .managementNumber(paymentHistory.getManagementNumber())
                .type(paymentHistory.getPaymentStatusName())
                .payAmount(paymentHistory.getPayAmount())
                .taxAmount(paymentHistory.getTax())
                .cardInfoData(CardInfoData.builder()
                        .cardNumber(cardNumberBlocker(cardInfo.getCardNumber().toString()))
                        .duration(cardInfo.getDuration())
                        .cvc(cardInfo.getCvc())
                        .build())
                .build();
    }

    private static String cardNumberBlocker(String cardNumberString) {
        StringBuilder starBlocks = new StringBuilder();
        for (int i = 0; i < cardNumberString.length() - 9; i ++) {
            starBlocks.append("*");
        }

        return cardNumberString.substring(0, 6)
                + starBlocks.toString()
                + cardNumberString.substring(cardNumberString.length() - 3);
    }
}
