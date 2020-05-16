package com.kakao.preinterview.payment.domain.encrypt;

public class FakeEncryptedValue implements EncryptedValue {
    @Override
    public String getEncryptedValue() {
        return "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY";
    }
}
