package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CardCompanyInfoRepository cardCompanyInfoRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

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
}
