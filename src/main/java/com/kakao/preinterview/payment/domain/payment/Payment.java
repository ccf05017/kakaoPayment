package com.kakao.preinterview.payment.domain.payment;

import java.math.BigDecimal;

public class Payment {
    private ManagementNumber managementNumber;
    private ManagementNumber relatedManagementNumber;
    private PayInfo payInfo;
    private CardInfo cardInfo;
    private Tax tax;

    protected Payment(
            ManagementNumber managementNumber,
            ManagementNumber relatedManagementNumber,
            PayInfo payInfo,
            CardInfo cardInfo,
            Tax tax
    ) {
        this.managementNumber = managementNumber;
        this.relatedManagementNumber = relatedManagementNumber;
        this.payInfo = payInfo;
        this.cardInfo = cardInfo;
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

    public String getPayStatusCardCompanyName() {
        return this.payInfo.getPayType().getCardCompanyName();
    }

    private Integer getCvc() {
        return this.cardInfo.getCvc();
    }

    public BigDecimal getTaxValue() {
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

    public String getPayTypeName() {
        return this.payInfo.getPayType().getName();
    }

    protected PayInfo getPayInfo() {
        return this.payInfo;
    }
}
