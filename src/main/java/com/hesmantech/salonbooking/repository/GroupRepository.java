package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID>, QuerydslPredicateExecutor<GroupEntity> {
}
