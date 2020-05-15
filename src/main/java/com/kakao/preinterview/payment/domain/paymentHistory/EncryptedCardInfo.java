package com.kakao.preinterview.payment.domain.paymentHistory;

public class EncryptedCardInfo {
    private String encryptedValue;

    protected EncryptedCardInfo(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public static EncryptedCardInfo create(CardInfo cardInfo, String key) throws Exception {
        return new EncryptedCardInfo(cardInfo.encrypt(key));
    }

    public String getEncryptedValue() {
        return encryptedValue;
    }

    public Long getCardNumber(String key) throws Exception {
        return CardInfo.decrypt(this.encryptedValue, key).getCardNumber();
    }
}
