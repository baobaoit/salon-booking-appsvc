package com.hesmantech.salonbooking.repository.searchparamsbuilder.base;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SearchParamsBuilder {
    Optional<BooleanExpression> getCommonCriteria();

    Pageable getPageable();
}
