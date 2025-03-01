package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.CreditConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditConfigurationRepository extends JpaRepository<CreditConfigurationEntity, UUID> {
    Optional<CreditConfigurationEntity> findTopByOrderByCreatedDateDesc();
}
