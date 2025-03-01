package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditRequest;
import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QCreditEntity.creditEntity;

public class CustomerCreditSearchParamsBuilder extends AbstractSearchParamsBuilder {
    private final CustomerCreditSortProperty sortProperty;
    private final String customerName;

    private CustomerCreditSearchParamsBuilder(Builder builder) {
        super(builder);
        this.sortProperty = builder.sortProperty;
        this.customerName = builder.customerName;
    }

    public static CustomerCreditSearchParamsBuilder from(int page, int size, Sort.Direction direction,
                                                         CustomerCreditSortProperty sortProperty,
                                                         SearchCustomerCreditRequest request) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withSortProperty(sortProperty)
                .withCustomerName(request.customerName())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        return Stream.of(
                        Optional.ofNullable(customerName).filter(StringUtils::isNotBlank)
                                .map(this::wrapInPercentSymbols)
                                .map(creditEntity.customer.firstName
                                        .concat(" ")
                                        .concat(creditEntity.customer.lastName)::likeIgnoreCase))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::or);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(sortProperty.getDbColumn());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private CustomerCreditSortProperty sortProperty;
        private String customerName;

        public Builder withSortProperty(CustomerCreditSortProperty sortProperty) {
            this.sortProperty = sortProperty;
            return this;
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public CustomerCreditSearchParamsBuilder build() {
            return new CustomerCreditSearchParamsBuilder(this);
        }
    }
}
