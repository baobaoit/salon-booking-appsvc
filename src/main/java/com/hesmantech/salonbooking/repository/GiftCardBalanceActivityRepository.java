package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.GiftCardBalanceActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface GiftCardBalanceActivityRepository extends JpaRepository<GiftCardBalanceActivityEntity, UUID>,
        QuerydslPredicateExecutor<GiftCardBalanceActivityEntity> {
}
