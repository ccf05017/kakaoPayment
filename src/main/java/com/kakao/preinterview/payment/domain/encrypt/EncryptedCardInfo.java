package com.kakao.preinterview.payment.domain.encrypt;

import com.kakao.preinterview.payment.domain.payment.CardInfo;

public class EncryptedCardInfo implements EncryptedValue {
    private String encryptedValue;

    protected EncryptedCardInfo(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public static EncryptedCardInfo create(CardInfo cardInfo, String key) throws Exception {
        String encryptedValue = EncryptDecrypt.encryptAES256(cardInfo.toEncryptRawData(), key);
        return new EncryptedCardInfo(encryptedValue);
    }

    public static String decryptFromRawData(String encryptedRawData, String key) throws Exception {
        return EncryptDecrypt.decryptAES256(encryptedRawData, key);
    }

    public String decrypt(String key) throws Exception {
        return EncryptDecrypt.decryptAES256(this.encryptedValue, key);
    }

    public String getEncryptedValue() {
        return encryptedValue;
    }
}
