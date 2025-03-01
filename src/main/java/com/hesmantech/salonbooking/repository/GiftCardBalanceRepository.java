package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GiftCardBalanceRepository extends JpaRepository<GiftCardBalanceEntity, UUID> {
}
