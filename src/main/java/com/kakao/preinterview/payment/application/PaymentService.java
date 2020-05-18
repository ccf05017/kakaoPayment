package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.PayType;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.exceptions.TryCancelFromCanceledPaymentException;
import com.kakao.preinterview.payment.domain.service.PaymentPartialCancelService;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CardCompanyInfoRepository cardCompanyInfoRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final PaymentPartialCancelService paymentPartialCancelService;

    @Value("${encryption.key}")
    private String key;

    @Transactional
    public PaymentHistory doPay(DoPayRequestDto resource) throws Exception {
        PaymentCreationStrategy paymentCreationStrategy = PaymentCreationStrategy.select(resource);
        Payment payment = paymentCreationStrategy.create(resource);

        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), key);
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(payment, encryptedCardInfo);
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);

        cardCompanyInfoRepository.save(cardCompanyInfo);
        PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);

        return savedPaymentHistory;
    }

    @Transactional
    public PaymentHistory cancelAll(PayCancelRequestDto resource) throws Exception {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber())
                .orElseThrow(NotExistPaymentHistoryException::new);

        Payment paymentCancel = PaymentCancelCreationStrategy.select(resource).create(paymentHistory, key, resource);
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(paymentCancel.getCardInfo(), key);
        PaymentHistory paymentCancelHistory = new PaymentHistory(paymentCancel, encryptedCardInfo);
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(paymentCancel, encryptedCardInfo);

        cardCompanyInfoRepository.save(cardCompanyInfo);
        PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentCancelHistory);
        paymentHistoryService.toCancelHistory(paymentHistory.getManagementNumber());

        return savedPaymentHistory;
    }

    public PaymentHistory cancelPartial(PayCancelRequestDto resource) throws Exception {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByManagementNumber(
                resource.getManagementNumber()).orElseThrow(NotExistPaymentHistoryException::new);

        requestToCanceledValidation(resource.getManagementNumber());
        requestToPaymentCancelHistoryValidation(paymentHistory);

        List<PaymentHistory> paymentHistories = paymentHistoryRepository
                .findAllByRelatedManagementNumberAndPaymentTypeName(
                        resource.getManagementNumber(), PayType.PAY_PARTIAL_CANCEL.getName());

        Payment paymentPartialCancel = paymentPartialCancelService.doPartialCancel(paymentHistory, key, resource,
                calculateAmountRemainSum(paymentHistory, paymentHistories),
                calculateTaxRemainSum(paymentHistory, paymentHistories));

        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(paymentPartialCancel.getCardInfo(), key);
        PaymentHistory paymentPartialCancelHistory = new PaymentHistory(paymentPartialCancel, encryptedCardInfo);
        CardCompanyInfo paymentPartialCancelCardCompanyInfo = CardCompanyInfo
                .createCardCompanyInfo(paymentPartialCancel, encryptedCardInfo);

        cardCompanyInfoRepository.save(paymentPartialCancelCardCompanyInfo);
        PaymentHistory savedHistory = paymentHistoryRepository.save(paymentPartialCancelHistory);
        paymentHistoryService.toCancelHistory(paymentHistory.getManagementNumber());

        return savedHistory;
    }

    private void requestToCanceledValidation(String managementNumber) {
        Long count = paymentHistoryRepository.countAllByRelatedManagementNumberAndPaymentTypeName(
                managementNumber, PayType.PAY_CANCEL.getName());
        if (count > 0) throw new TryCancelFromCanceledPaymentException();
    }

    private void requestToPaymentCancelHistoryValidation(PaymentHistory paymentHistory) {
        if (PayType.PAY_CANCEL.getName().equals(paymentHistory.getPaymentTypeName())
                || PayType.PAY_PARTIAL_CANCEL.getName().equals(paymentHistory.getPaymentTypeName())
        ) throw new TryCancelFromCanceledPaymentException();
    }

    private BigDecimal calculateAmountRemainSum(PaymentHistory original,  List<PaymentHistory> paymentHistories) {
        if (paymentHistories.size() == 0) return original.getPayAmount();
        BigDecimal partialAmountSum = paymentHistories.stream()
                .map(PaymentHistory::getPayAmount)
                .reduce(BigDecimal::add).get();
        return original.getPayAmount().subtract(partialAmountSum);
    }

    private BigDecimal calculateTaxRemainSum(PaymentHistory original, List<PaymentHistory> paymentHistories) {
        if (paymentHistories.size() == 0) return original.getTax();
        BigDecimal partialTaxSum = paymentHistories.stream()
                .map(PaymentHistory::getTax)
                .reduce(BigDecimal::add).get();
        return original.getTax().subtract(partialTaxSum);
    }
}
