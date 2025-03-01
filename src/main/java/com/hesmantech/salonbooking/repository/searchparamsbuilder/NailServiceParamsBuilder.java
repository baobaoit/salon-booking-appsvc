package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceSearchRequest;
import com.hesmantech.salonbooking.api.dto.sort.service.ServiceSortProperty;
import com.hesmantech.salonbooking.domain.model.service.ServiceStatus;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QServiceEntity.serviceEntity;

public class NailServiceParamsBuilder extends AbstractSearchParamsBuilder {
    private final ServiceSortProperty sortProperty;
    private final String name;
    private final List<ServiceStatus> statuses;

    private NailServiceParamsBuilder(Builder builder) {
        super(builder);
        this.sortProperty = builder.sortProperty;
        this.name = builder.name;
        this.statuses = builder.statuses;
    }

    public static NailServiceParamsBuilder from(int page, int size, Sort.Direction direction,
                                                ServiceSortProperty sortProperty, NailServiceSearchRequest searchRequest) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withSortProperty(sortProperty)
                .withName(searchRequest.name())
                .withStatuses(searchRequest.statuses())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        return Stream.of(
                        Optional.ofNullable(name).map(this::wrapInPercentSymbols)
                                .map(serviceEntity.name::likeIgnoreCase),
                        Optional.of(statuses).map(sttList -> sttList.isEmpty() ?
                                serviceEntity.status.ne(ServiceStatus.DELETED) :
                                serviceEntity.status.in(sttList)))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::and);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(sortProperty.getProperty());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private ServiceSortProperty sortProperty;
        private String name;
        private List<ServiceStatus> statuses;

        public Builder withSortProperty(ServiceSortProperty sortProperty) {
            this.sortProperty = sortProperty;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStatuses(List<ServiceStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public NailServiceParamsBuilder build() {
            return new NailServiceParamsBuilder(this);
        }
    }
}
