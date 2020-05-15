package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.utils.EncryptDecrypt;

import java.util.Objects;
import java.util.StringTokenizer;

public class CardInfo {
    private static final int MIN_CARD_NUMBER_LENGTH = 10;
    private static final int MAX_CARD_NUMBER_LENGTH = 16;
    private static final int DURATION_LENGTH = 4;
    private static final int CVC_LENGTH = 3;

    private Long cardNumber;
    private Integer duration;
    private Integer cvc;

    private CardInfo(long cardNumber, int duration, int cvc) {
        validation(cardNumber, duration, cvc);

        this.cardNumber = cardNumber;
        this.duration = duration;
        this.cvc = cvc;
    }

    private void validation(long cardNumber, int duration, int cvc) {
        this.cardNumberValidation(cardNumber);
        this.durationValidation(duration);
        this.cvcValidation(cvc);
    }

    private void cardNumberValidation(long cardNumber) {
        int cardNumberSize = (int)(Math.log10(cardNumber) + 1);
        if (cardNumberSize < MIN_CARD_NUMBER_LENGTH || cardNumberSize > MAX_CARD_NUMBER_LENGTH)
            throw new IllegalArgumentException("Invalid CardNumber");
    }

    private void durationValidation(int duration) {
        int durationSize = (int)(Math.log10(duration) + 1);
        if (durationSize != DURATION_LENGTH) throw new IllegalArgumentException("Invalid Duration");
    }

    private void cvcValidation(int cvc) {
        int cvcSize = (int)(Math.log10(cvc) + 1);
        if (cvcSize != CVC_LENGTH) throw new IllegalArgumentException("Invalid Cvc");
    }

    public static CardInfo create(long cardNumber, int duration, int cvc) {
        return new CardInfo(cardNumber, duration, cvc);
    }

    private String stringSum() {
        return this.cardNumber.toString() + "|"
                + this.duration.toString() + "|"
                + this.cvc.toString();
    }

    public String encrypt(String key) throws Exception {
        return EncryptDecrypt.encryptAES256(stringSum(), key);
    }

    public static CardInfo decrypt(String encryptString, String key) throws Exception {
        String decryptString = EncryptDecrypt.decryptAES256(encryptString, key);

        StringTokenizer stringTokenizer = new StringTokenizer(decryptString, "|");

        Long cardNumber = Long.parseLong(stringTokenizer.nextToken());
        Integer duration = Integer.parseInt(stringTokenizer.nextToken());
        Integer cvc = Integer.parseInt(stringTokenizer.nextToken());

        return new CardInfo(cardNumber, duration, cvc);
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getCvc() {
        return cvc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInfo cardInfo = (CardInfo) o;
        return Objects.equals(cardNumber, cardInfo.cardNumber) &&
                Objects.equals(duration, cardInfo.duration) &&
                Objects.equals(cvc, cardInfo.cvc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, duration, cvc);
    }
}
