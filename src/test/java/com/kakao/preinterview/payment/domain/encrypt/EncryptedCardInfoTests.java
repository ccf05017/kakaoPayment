package com.kakao.preinterview.payment.domain.encrypt;

import com.kakao.preinterview.payment.domain.payment.CardInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptedCardInfoTests {
    private CardInfo cardInfo;
    private String key;
    private Long cardNumber = 1111222233334444L;
    private String duration = "1122";
    private int cvc = 111;

    @BeforeEach
    public void setup() {
        cardInfo = CardInfo.create(cardNumber, duration, cvc);
        key = "TestKey";
    }

    @DisplayName("카드 정보를 입력 받아서 암호화 된 카드 정보를 만든다.")
    @Test
    void createTest() throws Exception {
        assertThat(EncryptedCardInfo.create(cardInfo, key)).isNotNull();
    }

    @DisplayName("암호화 된 데이터는 원문 내용을 알아볼 수 없다.")
    @Test
    void encryptionTest() throws Exception {
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(cardInfo, key);
        assertThat(encryptedCardInfo.getEncryptedValue()).doesNotContain(cardInfo.getCardNumber().toString());
        assertThat(encryptedCardInfo.getEncryptedValue()).doesNotContain(cardInfo.getCvc().toString());
        assertThat(encryptedCardInfo.getEncryptedValue()).doesNotContain(cardInfo.getDuration());
    }

    @DisplayName("암호화 된 데이터를 복호화 할 수 있다.")
    @Test
    void decryptTest() throws Exception {
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(cardInfo, key);
        String decrypt = encryptedCardInfo.decrypt(key);
        assertThat(decrypt).isEqualTo(cardNumber + "|" + duration + "|" + cvc);
    }
}
