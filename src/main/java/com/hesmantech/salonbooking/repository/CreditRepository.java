package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.repository.custom.CustomizedCreditRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditRepository extends JpaRepository<CreditEntity, UUID>, CustomizedCreditRepository {
}
