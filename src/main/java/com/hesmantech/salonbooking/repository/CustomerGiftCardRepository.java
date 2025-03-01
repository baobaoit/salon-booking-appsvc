package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.CustomerGiftCardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CustomerGiftCardRepository extends JpaRepository<CustomerGiftCardEntity, CustomerGiftCardId> {
    @Query("SELECT COALESCE(COUNT(c), 0) FROM CustomerGiftCardEntity c WHERE c.customer.id = ?1 AND c.redeemed IS TRUE")
    Integer countByCustomerIdAndRedeemedIsTrue(UUID customerId);
}
