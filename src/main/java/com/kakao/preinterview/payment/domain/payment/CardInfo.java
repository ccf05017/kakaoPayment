package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.payment.exceptions.InvalidCardInfoParamException;

import java.util.Objects;
import java.util.StringTokenizer;

public class CardInfo {
    private static final int MIN_CARD_NUMBER_LENGTH = 10;
    private static final int MAX_CARD_NUMBER_LENGTH = 16;
    private static final int DURATION_LENGTH = 4;
    private static final int CVC_LENGTH = 3;

    private Long cardNumber;
    private String duration;
    private Integer cvc;

    private CardInfo(long cardNumber, String duration, int cvc) {
        validation(cardNumber, duration, cvc);

        this.cardNumber = cardNumber;
        this.duration = duration;
        this.cvc = cvc;
    }

    public static CardInfo createFromDecryptedRawString(String decryptedRawString) {
        StringTokenizer stringTokenizer = new StringTokenizer(decryptedRawString, "|");

        Long cardNumber = Long.parseLong(stringTokenizer.nextToken());
        String duration = stringTokenizer.nextToken();
        Integer cvc = Integer.parseInt(stringTokenizer.nextToken());

        return new CardInfo(cardNumber, duration, cvc);
    }

    private void validation(long cardNumber, String duration, int cvc) {
        this.cardNumberValidation(cardNumber);
        this.durationValidation(duration);
        this.cvcValidation(cvc);
    }

    private void cardNumberValidation(long cardNumber) {
        int cardNumberSize = (int)(Math.log10(cardNumber) + 1);
        if (cardNumberSize < MIN_CARD_NUMBER_LENGTH || cardNumberSize > MAX_CARD_NUMBER_LENGTH)
            throw new InvalidCardInfoParamException("CardNumber");
    }

    private void durationValidation(String duration) {
        if (duration.length() != DURATION_LENGTH) throw new InvalidCardInfoParamException("Duration");
    }

    private void cvcValidation(int cvc) {
        int cvcSize = (int)(Math.log10(cvc) + 1);
        if (cvcSize != CVC_LENGTH) throw new InvalidCardInfoParamException("Cvc");
    }

    public static CardInfo create(long cardNumber, String duration, int cvc) {
        return new CardInfo(cardNumber, duration, cvc);
    }

    public String toEncryptRawData() {
        return this.cardNumber.toString() + "|"
                + this.duration + "|"
                + this.cvc.toString();
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public String getDuration() {
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
