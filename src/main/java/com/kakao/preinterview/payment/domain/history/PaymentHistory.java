package com.kakao.preinterview.payment.domain.history;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.exceptions.PaymentCancelCannotUpdateException;
import com.kakao.preinterview.payment.domain.payment.Payment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long revision;
    @Column(unique = true)
    private String managementNumber;
    private String relatedManagementNumber;
    private String encryptedCardInfo;
    private String installmentMonthFormatMonth;
    private BigDecimal payAmount;
    private BigDecimal tax;
    private String paymentTypeName;
    private boolean canceled;

    protected PaymentHistory(
            Long id,
            Long revision,
            String managementNumber,
            String relatedManagementNumber,
            String encryptedCardInfo,
            String installmentMonthFormatMonth,
            BigDecimal payAmount,
            BigDecimal tax,
            String paymentTypeName,
            boolean canceled
    ) {
        this.id = id;
        this.revision = revision;
        this.managementNumber = managementNumber;
        this.relatedManagementNumber = relatedManagementNumber;
        this.encryptedCardInfo = encryptedCardInfo;
        this.installmentMonthFormatMonth = installmentMonthFormatMonth;
        this.payAmount = payAmount;
        this.tax = tax;
        this.paymentTypeName = paymentTypeName;
        this.canceled = canceled;
    }

    public PaymentHistory(Payment payment, EncryptedCardInfo encryptedCardInfo) {
        this(
                null,
                0L,
                payment.getManagementNumberValue(),
                payment.getRelatedManagementNumberValue(),
                encryptedCardInfo.getEncryptedValue(),
                payment.getInstallmentMonthFormatMonth(),
                payment.getPayAmount(),
                payment.getTaxValue(),
                payment.getPayTypeName(),
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

    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public Long getRevision() {
        return revision;
    }

    public void toCanceled() {
        this.canceled = true;
    }

    public void revisionUp() {
        revisionValidation();
        this.revision += 1L;
    }

    private void revisionValidation() {
        if (!"PAY".equals(this.paymentTypeName)) throw new PaymentCancelCannotUpdateException();
    }
}
