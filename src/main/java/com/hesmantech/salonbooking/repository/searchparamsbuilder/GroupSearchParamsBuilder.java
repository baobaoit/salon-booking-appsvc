package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.group.SearchGroupRequest;
import com.hesmantech.salonbooking.api.dto.sort.group.GroupSortProperty;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QGroupEntity.groupEntity;

public class GroupSearchParamsBuilder extends AbstractSearchParamsBuilder {
    private final GroupSortProperty sortProperty;
    private final String name;

    private GroupSearchParamsBuilder(Builder builder) {
        super(builder);
        this.sortProperty = builder.sortProperty;
        this.name = builder.name;
    }

    public static GroupSearchParamsBuilder from(int page, int size, Sort.Direction direction,
                                                GroupSortProperty sortProperty, SearchGroupRequest searchGroupRequest) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withSortProperty(sortProperty)
                .withName(searchGroupRequest.name())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        return Stream.of(
                        Optional.ofNullable(name).map(this::wrapInPercentSymbols)
                                .map(groupEntity.name::likeIgnoreCase))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::and);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(sortProperty.getProperty());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private GroupSortProperty sortProperty;
        private String name;

        public Builder withSortProperty(GroupSortProperty sortProperty) {
            this.sortProperty = sortProperty;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public GroupSearchParamsBuilder build() {
            return new GroupSearchParamsBuilder(this);
        }
    }
}
