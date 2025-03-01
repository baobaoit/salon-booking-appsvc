package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderedDetailsRepository extends JpaRepository<OrderedDetailsEntity, UUID> {
}
