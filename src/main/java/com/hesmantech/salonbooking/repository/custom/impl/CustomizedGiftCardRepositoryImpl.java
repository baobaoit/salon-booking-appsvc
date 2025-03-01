package com.hesmantech.salonbooking.repository.custom.impl;

import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.QCustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.QGiftCardEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.repository.custom.CustomizedGiftCardRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CustomizedGiftCardRepositoryImpl implements CustomizedGiftCardRepository {
    private static final QGiftCardEntity giftCard = QGiftCardEntity.giftCardEntity;
    private static final QCustomerGiftCardEntity customerGiftCard = QCustomerGiftCardEntity.customerGiftCardEntity;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<GiftCardEntity> findAllGiftCards(Predicate predicate, Pageable pageable) {
        JPAQuery<Tuple> baseQuery = new JPAQueryFactory(entityManager)
                .select(giftCard, customerGiftCard)
                .from(giftCard)
                .leftJoin(customerGiftCard).on(giftCard.id.eq(customerGiftCard.giftCard.id));

        var query = baseQuery.clone();
        if (predicate != null) {
            query.where(predicate);
            baseQuery.where(predicate);
        }

        if (pageable != null) {
            addPaging(query, pageable);
        }

        List<GiftCardEntity> content = new ArrayList<>();
        query.fetch()
                .stream()
                .map(row -> {
                    var giftCardEntity = row.get(giftCard);
                    var customerGiftCardEntity = row.get(customerGiftCard);

                    if (giftCardEntity != null) {
                        var giftCardEntityClone = giftCardEntity.copy();
                        giftCardEntityClone.setCustomers(customerGiftCardEntity == null ? new HashSet<>() :
                                new HashSet<>(Set.of(customerGiftCardEntity)));
                        return giftCardEntityClone;
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(content::add);

        if (pageable != null) {
            long totalElements = baseQuery.fetch().size();
            return new PageImpl<>(content, pageable, totalElements);
        }
        return new PageImpl<>(content);
    }

    @Override
    public List<GiftCardEntity> findAllGiftCards(Predicate predicate) {
        return findAllGiftCards(predicate, null).getContent();
    }

    private void addPaging(JPAQuery<Tuple> query, Pageable pageable) {
        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        var giftCardEntityPath = new PathBuilder<>(GiftCardEntity.class, "giftCardEntity");
        for (var order : pageable.getSort()) {
            String orderProperty = order.getProperty();
            Order orderDirection = Order.valueOf(order.getDirection().name());
            GiftCardSortProperty property = GiftCardSortProperty.fromDbColumn(orderProperty);
            if (property != null) {
                switch (property) {
                    case DATE_ISSUED -> {
                        var dateIssuedPath = giftCardEntityPath.get(orderProperty, Instant.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, dateIssuedPath));
                    }
                    case CODE -> {
                        var codePath = giftCardEntityPath.get(orderProperty, String.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, codePath));
                    }
                    case STATUS -> {
                        var statusPath = giftCardEntityPath.get(orderProperty, GiftCardStatus.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, statusPath));
                    }
                    case INITIAL_VALUE -> {
                        var initialValue = giftCardEntityPath.get(orderProperty, Double.class);
                        query.orderBy(new OrderSpecifier<>(orderDirection, initialValue));
                    }
                    default -> throw new UnsupportedOperationException("Unsupported property: " + orderProperty);
                }
            }
        }
    }
}
