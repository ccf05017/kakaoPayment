package com.kakao.preinterview.payment.domain.history;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.exceptions.DuplicatedCancelException;
import com.kakao.preinterview.payment.domain.payment.Payment;

public class PaymentHistory {
    private Long id;
    private String managementNumber;
    private String relatedManagementNumber;
    private String encryptedCardInfo;
    private String installmentMonth;
    private String payAmount;
    private String paymentStatus;
    private boolean canceled;

    private PaymentHistory(
            Long id,
            String managementNumber,
            String relatedManagementNumber,
            String encryptedCardInfo,
            String installmentMonth,
            String payAmount,
            String paymentStatus,
            boolean canceled
    ) {
        this.id = id;
        this.managementNumber = managementNumber;
        this.relatedManagementNumber = relatedManagementNumber;
        this.encryptedCardInfo = encryptedCardInfo;
        this.installmentMonth = installmentMonth;
        this.payAmount = payAmount;
        this.paymentStatus = paymentStatus;
        this.canceled = canceled;
    }

    public PaymentHistory(Payment payment, EncryptedCardInfo encryptedCardInfo) {
        this(
                null,
                payment.getManagementNumberValue(),
                payment.getRelatedManagementNumberValue(),
                encryptedCardInfo.getEncryptedValue(),
                payment.getInstallmentMonthFormatMonth(),
                payment.getPayAmountString(),
                payment.getPayStatusName(),
                !payment.getPayStatusCardCompanyName().equals("PAYMENT")
        );
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

    public String getInstallmentMonth() {
        return installmentMonth;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void toCanceled() {
        if (this.canceled) throw new DuplicatedCancelException();
        this.canceled = true;
    }
}
