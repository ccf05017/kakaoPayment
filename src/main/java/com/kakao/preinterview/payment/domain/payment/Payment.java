package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.parser.CardCompanyDataParser;
import com.kakao.preinterview.payment.domain.payment.exceptions.ParseToCardCompanyDataException;

import java.math.BigDecimal;

public class Payment {
    private Long id;
    private ManagementNumber managementNumber;
    private ManagementNumber relatedManagementNumber;
    private PayInfo payInfo;
    private CardInfo cardInfo;
    private EncryptedCardInfo encryptedCardInfo;
    private Tax tax;

    protected Payment(
            Long id,
            ManagementNumber managementNumber,
            ManagementNumber relatedManagementNumber,
            PayInfo payInfo,
            CardInfo cardInfo,
            EncryptedCardInfo encryptedCardInfo,
            Tax tax
    ) {
        this.id = id;
        this.managementNumber = managementNumber;
        this.relatedManagementNumber = relatedManagementNumber;
        this.payInfo = payInfo;
        this.cardInfo = cardInfo;
        this.encryptedCardInfo = encryptedCardInfo;
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public ManagementNumber getManagementNumber() {
        return this.managementNumber;
    }

    public InstallmentMonth getInstallmentMonth() {
        return this.payInfo.getInstallmentMonth();
    }

    public BigDecimal getPayAmount() {
        return this.payInfo.getPayAmount();
    }

    public EncryptedCardInfo getEncryptedCardInfo() {
        return this.encryptedCardInfo;
    }

    public ManagementNumber getRelatedManagementNumber() {
        return this.relatedManagementNumber;
    }

    public CardInfo getCardInfo() {
        return this.cardInfo;
    }

    public Long getCardNumber() {
        return this.cardInfo.getCardNumber();
    }

    public String getDuration() {
        return this.cardInfo.getDuration();
    }

    private String getPayStatusName() {
        return this.payInfo.getPayStatus().getName();
    }

    private Integer getCvc() {
        return this.cardInfo.getCvc();
    }

    private BigDecimal getTaxValue() {
        return this.tax.getValue();
    }

    private String getRelatedManagementNumberValue() {
        if (this.relatedManagementNumber == null) return "";
        return this.relatedManagementNumber.getValue();
    }

    private String getManagementNumberValue() {
        return this.managementNumber.getValue();
    }

    private String getInstallmentMonthFormatMonth() {
        return this.payInfo.getInstallmentMonth().getFormatMonth();
    }

    public String parseToCardCompanyData() {
        String cardCompanyStringData = CardCompanyDataParser.parse(4, "446", "nd")
                + CardCompanyDataParser.parse(10, this.getPayStatusName(), "sl")
                + CardCompanyDataParser.parse(20, this.getManagementNumberValue(), "sl")
                + CardCompanyDataParser.parse(20, this.getCardNumber().toString(), "nl")
                + CardCompanyDataParser.parse(2, this.getInstallmentMonthFormatMonth(), "nr")
                + CardCompanyDataParser.parse(4, this.getDuration(), "nl")
                + CardCompanyDataParser.parse(3, this.getCvc().toString(), "nl")
                + CardCompanyDataParser.parse(10, this.getPayAmount().toString(), "nd")
                + CardCompanyDataParser.parse(10, this.getTaxValue().toString(), "nr")
                + CardCompanyDataParser.parse(20, this.getRelatedManagementNumberValue(), "sl")
                + CardCompanyDataParser.parse(300, this.encryptedCardInfo.getEncryptedValue(), "sl")
                + CardCompanyDataParser.parse(47, "", "sl");
        cardCompanyDataValidate(cardCompanyStringData);

        return cardCompanyStringData;
    }

    protected void cardCompanyDataValidate(String cardCompanyStringData) {
        if (cardCompanyStringData.length() != 450) throw new ParseToCardCompanyDataException();
    }
}
