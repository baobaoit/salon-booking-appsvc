package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID>, QuerydslPredicateExecutor<UserEntity> {
    @Query("SELECT u FROM UserEntity u WHERE u.userId = :username OR u.phoneNumber = :username and u.status = 'ACTIVE'")
    Optional<UserEntity> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u WHERE u.id = :id AND u.status <> 'DELETED'")
    Optional<UserEntity> findByIdAndStatusIsNotDELETED(@Param("id") UUID id);

    Optional<UserEntity> findByIdAndStatus(UUID id, UserStatus status);
}
