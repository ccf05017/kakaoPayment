package com.kakao.preinterview.payment.domain.history;

import com.kakao.preinterview.payment.domain.encrypt.EncryptedCardInfo;
import com.kakao.preinterview.payment.domain.payment.FakePaymentInfoFactory;
import com.kakao.preinterview.payment.domain.payment.Payment;

public class FakePaymentHistoryFactory {
    public static PaymentHistory createPaymentHistory() throws Exception {
        Payment fakePayment = FakePaymentInfoFactory.createFakePayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(fakePayment.getCardInfo(), "testKey");

        return new PaymentHistory(fakePayment, encryptedCardInfo);
    }

    public static PaymentHistory createPaymentCancelHistory() throws Exception {
        Payment fakePayment = FakePaymentInfoFactory.createFakeCancelPayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(fakePayment.getCardInfo(), "testKey");

        return new PaymentHistory(fakePayment, encryptedCardInfo);
    }

    public static PaymentHistory createPaymentCancelPartialHistory() throws Exception {
        Payment fakePayment = FakePaymentInfoFactory.createFakePartialCancelPayment();
        EncryptedCardInfo encryptedCardInfo = EncryptedCardInfo.create(fakePayment.getCardInfo(), "testKey");

        return new PaymentHistory(fakePayment, encryptedCardInfo);
    }
}
