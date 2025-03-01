package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, QuerydslPredicateExecutor<OrderEntity> {
    Optional<OrderEntity> findByIdAndCustomer_Id(UUID id, UUID userId);

    Optional<OrderEntity> findByIdAndStatusIn(UUID id, Collection<OrderStatus> statuses);

    long countByStatusInAndCustomer_StatusInAndCreatedDateBetween(Collection<OrderStatus> statuses,
                                                                  Collection<UserStatus> customerStatuses,
                                                                  Instant from, Instant to);

    @Query("SELECT COALESCE(SUM(o.price), 0) FROM OrderEntity o WHERE o.status = 'CHECK_OUT' AND o.customer.status IN ?1 AND " +
            "o.createdDate BETWEEN ?2 AND ?3")
    Double totalPriceByCustomerStatusInAndCreatedDateBetween(Collection<UserStatus> customerStatuses,
                                                             Instant from, Instant to);

    @Query("SELECT COALESCE(SUM(o.price), 0) FROM OrderEntity o WHERE o.status = 'CHECK_OUT' AND o.customer.id = ?1 AND " +
            "o.customer.status IN ?2")
    Double totalPriceByCustomerIdAndCustomerStatusIn(UUID customerId, Collection<UserStatus> customerStatuses);
}
