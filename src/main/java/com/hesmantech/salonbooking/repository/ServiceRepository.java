package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID>, QuerydslPredicateExecutor<ServiceEntity> {
}
