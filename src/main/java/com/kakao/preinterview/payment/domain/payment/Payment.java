package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Payment {
    private ManagementNumber managementNumber;
    private ManagementNumber relatedManagementNumber;
    private PayInfo payInfo;
    private CardInfo cardInfo;
    private EncryptedCardInfo encryptedCardInfo;
    private Tax tax;

    protected Payment(
            ManagementNumber managementNumber,
            ManagementNumber relatedManagementNumber,
            PayInfo payInfo,
            CardInfo cardInfo,
            EncryptedCardInfo encryptedCardInfo,
            Tax tax
    ) {
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

    public String getCardNumberString() {
        return this.cardInfo.getCardNumber().toString();
    }

    public String getDuration() {
        return this.cardInfo.getDuration();
    }

    public String getPayStatusName() {
        return this.payInfo.getPayStatus().getName();
    }

    private Integer getCvc() {
        return this.cardInfo.getCvc();
    }

    private BigDecimal getTaxValue() {
        return this.tax.getValue();
    }

    public String getRelatedManagementNumberValue() {
        if (this.relatedManagementNumber == null) return "";
        return this.relatedManagementNumber.getValue();
    }

    public String getManagementNumberValue() {
        return this.managementNumber.getValue();
    }

    public String getInstallmentMonthFormatMonth() {
        return this.payInfo.getInstallmentMonth().getFormatMonth();
    }

    public String getCvcString() {
        return this.getCvc().toString();
    }

    public String getPayAmountString() {
        return this.getPayAmount().toString();
    }

    public String getTaxValueString() {
        return this.getTaxValue().toString();
    }

    public String encryptedCardInfoString() {
        return this.getEncryptedCardInfo().getEncryptedValue();
    }
}
