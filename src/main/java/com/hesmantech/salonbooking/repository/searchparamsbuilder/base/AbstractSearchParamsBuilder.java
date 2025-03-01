package com.hesmantech.salonbooking.repository.searchparamsbuilder.base;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class AbstractSearchParamsBuilder implements SearchParamsBuilder {
    protected final int page;
    protected final int size;
    protected final Sort.Direction direction;

    protected AbstractSearchParamsBuilder(Builder<? extends Builder<?>> builder) {
        this.page = builder.page;
        this.size = builder.size;
        this.direction = builder.direction;
    }

    protected Pageable getPageable(String property) {
        return PageRequest.of(page, size, direction, property);
    }

    protected String wrapInPercentSymbols(String str) {
        return "%%%s%%".formatted(str);
    }

    public BooleanExpression getCommonCriteriaValue() {
        return getCommonCriteria().orElse(null);
    }

    @SuppressWarnings("unchecked")
    protected abstract static class Builder<T extends Builder<T>> {
        protected int page;
        protected int size;
        protected Sort.Direction direction;

        public T withPage(int page) {
            this.page = page;
            return (T) this;
        }

        public T withSize(int size) {
            this.size = size;
            return (T) this;
        }

        public T withDirection(Sort.Direction direction) {
            this.direction = direction;
            return (T) this;
        }
    }
}
