package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, UUID> {
}
