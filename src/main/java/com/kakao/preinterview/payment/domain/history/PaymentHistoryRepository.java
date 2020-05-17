package com.kakao.preinterview.payment.domain.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<PaymentHistory> findByManagementNumber(String managementNumber);
}
