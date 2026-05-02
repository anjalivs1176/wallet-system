package com.wallet.wallet_service.repository;

import com.wallet.wallet_service.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord,String> {
}
