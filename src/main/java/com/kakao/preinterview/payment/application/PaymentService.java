package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.service.PaymentPartialCancelService;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
    public String doPay(DoPayRequestDto resource) throws Exception {
        PaymentCreationStrategy paymentCreationStrategy = PaymentCreationStrategy.select(resource);
        Payment payment = paymentCreationStrategy.create(resource);

        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), key);
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(payment, encryptedCardInfo);
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);

        cardCompanyInfoRepository.save(cardCompanyInfo);
        paymentHistoryRepository.save(paymentHistory);

        return payment.getManagementNumberValue();
    }

    @Transactional
    public Payment cancelAll(PayCancelRequestDto resource) throws Exception {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber())
                .orElseThrow(NotExistPaymentHistoryException::new);

        Payment paymentCancel = PaymentCancelCreationStrategy.select(resource).create(paymentHistory, key, resource);
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(paymentCancel.getCardInfo(), key);
        PaymentHistory paymentCancelHistory = new PaymentHistory(paymentCancel, encryptedCardInfo);
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(paymentCancel, encryptedCardInfo);

        cardCompanyInfoRepository.save(cardCompanyInfo);
        paymentHistoryRepository.save(paymentCancelHistory);
        paymentHistoryService.toCancelHistory(paymentHistory.getManagementNumber());

        return paymentCancel;
    }

    public PaymentHistory cancelPartial(PayCancelRequestDto resource) throws Exception {
        // TODO: 헷갈리니까 도메인부터 제대로 구현하고 다시 돌아올 것
        PaymentHistory paymentHistory = paymentHistoryRepository.findByManagementNumber(resource.getManagementNumber())
                .orElseThrow(NotExistPaymentHistoryException::new);
        BigDecimal amountRemainSum = BigDecimal.ZERO;
        BigDecimal taxRemainSum = BigDecimal.ZERO;
        Payment partialCancelPayment = paymentPartialCancelService.doByAutoTax(paymentHistory, key, resource,
                amountRemainSum, taxRemainSum);

        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(partialCancelPayment.getCardInfo(), key);
        PaymentHistory partialCancelPaymentHistory = new PaymentHistory(partialCancelPayment, encryptedCardInfo);

        paymentHistoryService.upRevision(partialCancelPaymentHistory.getManagementNumber());

        return paymentHistoryRepository.save(partialCancelPaymentHistory);
    }
}
