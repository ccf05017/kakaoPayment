package com.kakao.preinterview.payment.domain.payment;

public class EncryptedCardInfo {
    private String encryptedValue;

    public EncryptedCardInfo(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public static EncryptedCardInfo create(CardInfo cardInfo, String key) throws Exception {
        return new EncryptedCardInfo(cardInfo.encrypt(key));
    }
}
