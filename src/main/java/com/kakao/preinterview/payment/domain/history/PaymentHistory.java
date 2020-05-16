package com.kakao.preinterview.payment.domain.history;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.exceptions.DuplicatedCancelException;
import com.kakao.preinterview.payment.domain.payment.Payment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String managementNumber;
    private String relatedManagementNumber;
    private String encryptedCardInfo;
    private String installmentMonthFormatMonth;
    private BigDecimal payAmount;
    private BigDecimal tax;
    private String paymentStatusName;
    private boolean canceled;

    protected PaymentHistory(
            Long id,
            String managementNumber,
            String relatedManagementNumber,
            String encryptedCardInfo,
            String installmentMonthFormatMonth,
            BigDecimal payAmount,
            BigDecimal tax,
            String paymentStatusName,
            boolean canceled
    ) {
        this.id = id;
        this.managementNumber = managementNumber;
        this.relatedManagementNumber = relatedManagementNumber;
        this.encryptedCardInfo = encryptedCardInfo;
        this.installmentMonthFormatMonth = installmentMonthFormatMonth;
        this.payAmount = payAmount;
        this.tax = tax;
        this.paymentStatusName = paymentStatusName;
        this.canceled = canceled;
    }

    public PaymentHistory(Payment payment, EncryptedCardInfo encryptedCardInfo) {
        this(
                null,
                payment.getManagementNumberValue(),
                payment.getRelatedManagementNumberValue(),
                encryptedCardInfo.getEncryptedValue(),
                payment.getInstallmentMonthFormatMonth(),
                payment.getPayAmount(),
                payment.getTaxValue(),
                payment.getPayStatusName(),
                !payment.getPayStatusCardCompanyName().equals("PAYMENT")
        );
    }

    public Long getId() {
        return id;
    }

    public String getManagementNumber() {
        return managementNumber;
    }

    public String getRelatedManagementNumber() {
        return relatedManagementNumber;
    }

    public String getEncryptedCardInfo() {
        return encryptedCardInfo;
    }

    public String getInstallmentMonthFormatMonth() {
        return installmentMonthFormatMonth;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public BigDecimal getTax() {
        return this.tax;
    }

    public String getPaymentStatusName() {
        return paymentStatusName;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void toCanceled() {
        if (this.canceled) throw new DuplicatedCancelException();
        this.canceled = true;
    }
}
