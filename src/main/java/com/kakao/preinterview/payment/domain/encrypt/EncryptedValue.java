package com.kakao.preinterview.payment.domain.encrypt;

public interface EncryptedValue {
    String getEncryptedValue();
    String decrypt(String key) throws Exception;
}
