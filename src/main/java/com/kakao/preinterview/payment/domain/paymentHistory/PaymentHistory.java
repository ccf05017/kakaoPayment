package com.kakao.preinterview.payment.domain.paymentHistory;

import java.math.BigDecimal;

public class PaymentHistory {
    private Long id;
    private ManagementNumber managementNumber;
    private ManagementNumber relatedManagementNumber;
    private PayInfo payInfo;
    private EncryptedCardInfo encryptedCardInfo;
    private Tax tax;

    protected PaymentHistory(
            Long id,
            ManagementNumber managementNumber,
            ManagementNumber relatedManagementNumber,
            PayInfo payInfo,
            EncryptedCardInfo encryptedCardInfo,
            Tax tax
    ) {
        this.id = id;
        this.managementNumber = managementNumber;
        this.relatedManagementNumber = relatedManagementNumber;
        this.payInfo = payInfo;
        this.encryptedCardInfo = encryptedCardInfo;
        this.tax = tax;
    }

    public Tax getTax() {
        return tax;
    }

    public ManagementNumber getManagementNumber() {
        return this.managementNumber;
    }

    public int getInstallmentMonths() {
        return this.payInfo.getInstallmentMonths();
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
}
