package com.kakao.preinterview.payment.domain.payment;

import com.kakao.preinterview.payment.domain.parser.ParserType;

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

    public String getCardNumberForCardCompany() {
        return ParserType.NUMBER_LEFT.parse(20, this.getCardNumber().toString());
    }

    public String getInstallmentMonthForCardCompany() {
        return ParserType.NUMBER_DEFAULT.parse(2, this.getInstallmentMonth().getFormatMonth());
    }

    public String getDurationForCardCompany() {
        return ParserType.NUMBER_LEFT.parse(4, this.getDuration());
    }
}
