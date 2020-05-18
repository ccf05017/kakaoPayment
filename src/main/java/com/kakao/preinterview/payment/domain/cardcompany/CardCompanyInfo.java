package com.kakao.preinterview.payment.domain.cardcompany;

import com.kakao.preinterview.payment.domain.cardcompany.exceptions.ParseToCardCompanyDataException;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedValue;
import com.kakao.preinterview.payment.domain.parser.CardCompanyDataParser;
import com.kakao.preinterview.payment.domain.payment.Payment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardCompanyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000)
    private String stringData;

    private CardCompanyInfo(Long id, String stringData) {
        validateStringDataLength(stringData);
        this.id = id;
        this.stringData = stringData;
    }

    public static CardCompanyInfo createCardCompanyInfo(Payment payment, EncryptedValue encryptedValue) {
        String cardCompanyStringData = CardCompanyDataParser.parse(4, "446", "nd")
                + CardCompanyDataParser.parse(10, payment.getPayStatusCardCompanyName(), "sl")
                + CardCompanyDataParser.parse(20, payment.getManagementNumberValue(), "sl")
                + CardCompanyDataParser.parse(20, payment.getCardNumberString(), "nl")
                + CardCompanyDataParser.parse(2, payment.getInstallmentMonthFormatMonth(), "nr")
                + CardCompanyDataParser.parse(4, payment.getDuration(), "nl")
                + CardCompanyDataParser.parse(3, payment.getCvcString(), "nl")
                + CardCompanyDataParser.parse(10, payment.getPayAmountFormatString(), "nd")
                + CardCompanyDataParser.parse(10, payment.getTaxAmountFormatString(), "nr")
                + CardCompanyDataParser.parse(20, payment.getRelatedManagementNumberValue(), "sl")
                + CardCompanyDataParser.parse(300, encryptedValue.getEncryptedValue(), "sl")
                + CardCompanyDataParser.parse(47, "", "sl");

        return new CardCompanyInfo(null, cardCompanyStringData);
    }

    public Long getId() {
        return id;
    }

    protected void validateStringDataLength(String cardCompanyStringData) {
        if (cardCompanyStringData.length() != 450) throw new ParseToCardCompanyDataException();
    }

    public String getStringData() {
        return stringData;
    }
}
