package com.kakao.preinterview.payment.application;

import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfo;
import com.kakao.preinterview.payment.domain.cardcompany.CardCompanyInfoRepository;
import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.history.PaymentHistory;
import com.kakao.preinterview.payment.domain.history.PaymentHistoryRepository;
import com.kakao.preinterview.payment.domain.payment.InstallmentMonth;
import com.kakao.preinterview.payment.domain.payment.PayStatus;
import com.kakao.preinterview.payment.domain.payment.Payment;
import com.kakao.preinterview.payment.domain.payment.PaymentFactory;
import com.kakao.preinterview.payment.ui.dto.DoPayRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final CardCompanyInfoRepository cardCompanyInfoRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public String doPay(DoPayRequestDto resource) throws Exception {
        Payment payment;

        if (resource.getTax() != null) {
            payment = PaymentFactory.createPaymentManualTax(
                    InstallmentMonth.createFromMonth(resource.getInstallmentMonth()),
                    BigDecimal.valueOf(resource.getPayAmount()),
                    PayStatus.PAY,
                    resource.getCardNumber(),
                    resource.getDuration(),
                    resource.getCvc(),
                    resource.getTax()
            );
        } else {
            payment = PaymentFactory.createPaymentAutoTax(
                    InstallmentMonth.createFromMonth(resource.getInstallmentMonth()),
                    BigDecimal.valueOf(resource.getPayAmount()),
                    PayStatus.PAY,
                    resource.getCardNumber(),
                    resource.getDuration(),
                    resource.getCvc()
            );
        }
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(payment.getCardInfo(), "testKey");
        CardCompanyInfo cardCompanyInfo = CardCompanyInfo.createCardCompanyInfo(payment, encryptedCardInfo);
        PaymentHistory paymentHistory = new PaymentHistory(payment, encryptedCardInfo);

        cardCompanyInfoRepository.save(cardCompanyInfo);
        paymentHistoryRepository.save(paymentHistory);

        return payment.getManagementNumberValue();
    }
}
