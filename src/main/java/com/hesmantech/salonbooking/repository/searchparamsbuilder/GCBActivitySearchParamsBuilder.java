package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcardbalance.GCBActivitySortProperty;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QGiftCardBalanceActivityEntity.giftCardBalanceActivityEntity;

public class GCBActivitySearchParamsBuilder extends AbstractSearchParamsBuilder {
    private final GCBActivitySortProperty sortProperty;
    private final UUID customerId;

    private GCBActivitySearchParamsBuilder(Builder builder) {
        super(builder);
        this.sortProperty = builder.sortProperty;
        this.customerId = builder.customerId;
    }

    public static GCBActivitySearchParamsBuilder from(int page, int size, Sort.Direction direction,
                                                      GCBActivitySortProperty sortProperty,
                                                      SearchGCBActivityRequest request) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withSortProperty(sortProperty)
                .withCustomerId(request.customerId())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        return Stream.of(
                        Optional.ofNullable(customerId).map(giftCardBalanceActivityEntity.giftCardBalance.id::eq))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::and);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(sortProperty.getProperty());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private GCBActivitySortProperty sortProperty;
        private UUID customerId;

        public Builder withSortProperty(GCBActivitySortProperty sortProperty) {
            this.sortProperty = sortProperty;
            return this;
        }

        public Builder withCustomerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public GCBActivitySearchParamsBuilder build() {
            return new GCBActivitySearchParamsBuilder(this);
        }
    }
}
