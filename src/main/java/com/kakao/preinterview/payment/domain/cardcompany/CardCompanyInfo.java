package com.kakao.preinterview.payment.domain.cardcompany;

import com.kakao.preinterview.payment.domain.cardcompany.exceptions.ParseToCardCompanyDataException;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedValue;
import com.kakao.preinterview.payment.domain.parser.CardCompanyDataParser;
import com.kakao.preinterview.payment.domain.payment.Payment;

public class CardCompanyInfo {
    private Long id;
    private String stringData;

    private CardCompanyInfo(Long id, String stringData) {
        validateStringDataLength(stringData);
        this.id = id;
        this.stringData = stringData;
    }

    public static CardCompanyInfo createCardCompanyInfo(Payment payment, EncryptedValue encryptedValue) {
        String cardCompanyStringData = CardCompanyDataParser.parse(4, "446", "nd")
                + CardCompanyDataParser.parse(10, payment.getPayStatusName(), "sl")
                + CardCompanyDataParser.parse(20, payment.getManagementNumberValue(), "sl")
                + CardCompanyDataParser.parse(20, payment.getCardNumberString(), "nl")
                + CardCompanyDataParser.parse(2, payment.getInstallmentMonthFormatMonth(), "nr")
                + CardCompanyDataParser.parse(4, payment.getDuration(), "nl")
                + CardCompanyDataParser.parse(3, payment.getCvcString(), "nl")
                + CardCompanyDataParser.parse(10, payment.getPayAmountString(), "nd")
                + CardCompanyDataParser.parse(10, payment.getTaxValueString(), "nr")
                + CardCompanyDataParser.parse(20, payment.getRelatedManagementNumberValue(), "sl")
                + CardCompanyDataParser.parse(300, encryptedValue.getEncryptedValue(), "sl")
                + CardCompanyDataParser.parse(47, "", "sl");

        return new CardCompanyInfo(null, cardCompanyStringData);
    }

    protected void validateStringDataLength(String cardCompanyStringData) {
        if (cardCompanyStringData.length() != 450) throw new ParseToCardCompanyDataException();
    }

    public String getStringData() {
        return stringData;
    }
}
