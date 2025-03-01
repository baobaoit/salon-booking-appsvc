package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.repository.custom.CustomizedGiftCardRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface GiftCardRepository extends JpaRepository<GiftCardEntity, UUID>, CustomizedGiftCardRepository {
    boolean existsByCode(String code);

    Optional<GiftCardEntity> findByIdAndStatus(UUID id, GiftCardStatus status);

    @Query("SELECT COALESCE(COUNT(g), 0) FROM GiftCardEntity g LEFT JOIN g.customers c WHERE c.customer.id = ?1 AND " +
            "(g.status = 'PARTIAL' OR (g.status = 'FULL' AND c.redeemed IS FALSE) OR (g.status = 'FULL' AND c.redeemed IS TRUE))")
    int countTotalNoGiftCard(UUID customerId);

    @Query("SELECT COALESCE(COUNT(g), 0) FROM GiftCardEntity g LEFT JOIN g.customers c WHERE c.customer.id = ?1 AND " +
            "g.status = 'FULL' AND c.redeemed IS FALSE")
    int countAvailableNoGiftCard(UUID customerId);

    Optional<GiftCardEntity> findByCodeAndStatus(String code, GiftCardStatus status);
}
