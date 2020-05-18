package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.application.exceptions.NotExistPaymentHistoryException;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import com.kakao.preinterview.payment.ui.dto.PayCancelRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CardCompanyInfoRepository cardCompanyInfoRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryService paymentHistoryService;

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

    public PaymentHistory cancelPartial(PayCancelRequestDto resource) {
        return null;
    }
}
