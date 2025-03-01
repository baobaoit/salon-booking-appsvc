package com.hesmantech.salonbooking.repository.custom.impl;

import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.domain.QCreditEntity;
import com.hesmantech.salonbooking.domain.QUserEntity;
import com.hesmantech.salonbooking.repository.custom.CustomizedCreditRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public class CustomizedCreditRepositoryImpl implements CustomizedCreditRepository {
    private static final QCreditEntity creditEntity = QCreditEntity.creditEntity;
    private static final QUserEntity customerEntity = QUserEntity.userEntity;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CreditEntity> findAllCredits(Predicate predicate, Pageable pageable) {
        JPAQuery<CreditEntity> baseQuery = new JPAQuery<>(entityManager)
                .select(creditEntity)
                .from(creditEntity)
                .leftJoin(customerEntity).on(creditEntity.customer.id.eq(customerEntity.id));

        var query = baseQuery.clone();
        if (predicate != null) {
            query.where(predicate);
            baseQuery.where(predicate);
        }

        addPaging(query, pageable);

        List<CreditEntity> content = query.fetch();
        long totalElements = baseQuery.fetch().size();
        return new PageImpl<>(content, pageable, totalElements);
    }

    @Override
    public Page<CreditEntity> findAllCredits(Pageable pageable) {
        return findAllCredits(null, pageable);
    }

    private void addPaging(JPAQuery<CreditEntity> query, Pageable pageable) {
        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        var creditEntityPath = new PathBuilder<>(CreditEntity.class, "creditEntity");
        for (var order : pageable.getSort()) {
            String orderProperty = order.getProperty();
            Order orderDirection = Order.valueOf(order.getDirection().name());
            CustomerCreditSortProperty property = CustomerCreditSortProperty.fromDbColumn(orderProperty);
            if (property != null) {
                switch (property) {
                    case CREATED_DATE -> {
                        var createdDatePath = creditEntityPath.get(orderProperty, Instant.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, createdDatePath));
                    }
                    case AVAILABLE_CREDIT, TOTAL_CREDIT -> {
                        var doublePath = creditEntityPath.get(orderProperty, Double.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, doublePath));
                    }
                    case AVAILABLE_NO_GIFT_CARD, REDEEM_NO_GIFT_CARD -> {
                        var intPath = creditEntityPath.get(orderProperty, Integer.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, intPath));
                    }
                    case CUSTOMER_NAME -> {
                        for (String stringOrderProperty : orderProperty.split(",")) {
                            var stringPath = creditEntityPath.get(stringOrderProperty, String.class);
                            query.orderBy(new OrderSpecifier<>(orderDirection, stringPath));
                        }
                    }
                    default -> throw new UnsupportedOperationException("Unsupported property: " + orderProperty);
                }
            }
        }
    }
}
