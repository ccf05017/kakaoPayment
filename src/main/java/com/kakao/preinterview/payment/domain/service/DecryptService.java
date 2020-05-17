package com.kakao.preinterview.payment.domain.service;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.payment.CardInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DecryptService {
    @Value("${encryption.key}")
    private String key;

    public CardInfo getCardInfoFromPaymentHistory(PaymentHistory paymentHistory) throws Exception {
        String decryptedCardData = EncryptedCardInfo.decryptFromRawData(paymentHistory.getEncryptedCardInfo(), key);

        return CardInfo.createFromDecryptedRawString(decryptedCardData);
    }
}
