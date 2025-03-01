package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import com.hesmantech.salonbooking.domain.QCustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QGiftCardEntity.giftCardEntity;

public class CustomerGiftCardSearchParamsBuilder extends AbstractSearchParamsBuilder {
    private final GiftCardSortProperty sortProperty;
    private final String code;
    private final Set<GiftCardStatus> statuses;

    private CustomerGiftCardSearchParamsBuilder(Builder builder) {
        super(builder);
        this.sortProperty = builder.sortProperty;
        this.code = builder.code;
        this.statuses = builder.statuses;
    }

    public static CustomerGiftCardSearchParamsBuilder from(int page, int size, Sort.Direction direction,
                                                           GiftCardSortProperty sortProperty, SearchGiftCardRequest request) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withSortProperty(sortProperty)
                .withCode(request.code())
                .withStatuses(request.statuses())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        return Stream.of(
                        Optional.ofNullable(code).filter(StringUtils::isNotBlank)
                                .map(this::wrapInPercentSymbols)
                                .map(giftCardEntity.code::likeIgnoreCase),
                        Optional.of(statuses).filter(sttList -> !sttList.isEmpty())
                                .map(sttList -> {
                                    if (sttList.size() == 1) {
                                        var predicate = giftCardEntity.status.eq(GiftCardStatus.FULL);
                                        var statusToCheck = sttList.iterator().next();

                                        var customerGiftCardEntity = QCustomerGiftCardEntity.customerGiftCardEntity;
                                        if (GiftCardStatus.REDEEMABLE.equals(statusToCheck)) {
                                            return predicate.and(customerGiftCardEntity.redeemed.isTrue());
                                        } else if (GiftCardStatus.FULL.equals(statusToCheck)) {
                                            return predicate.and(customerGiftCardEntity.redeemed.isFalse());
                                        }
                                    }

                                    return giftCardEntity.status.in(sttList);
                                }))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::and);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(sortProperty.getDbColumn());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private GiftCardSortProperty sortProperty;
        private String code;
        private Set<GiftCardStatus> statuses;

        public Builder withSortProperty(GiftCardSortProperty sortProperty) {
            this.sortProperty = sortProperty;
            return this;
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withStatuses(Set<GiftCardStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public CustomerGiftCardSearchParamsBuilder build() {
            return new CustomerGiftCardSearchParamsBuilder(this);
        }
    }
}
