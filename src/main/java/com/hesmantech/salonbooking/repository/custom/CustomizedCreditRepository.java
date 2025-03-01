package com.hesmantech.salonbooking.repository.custom;

import com.hesmantech.salonbooking.domain.CreditEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomizedCreditRepository {
    Page<CreditEntity> findAllCredits(Predicate predicate, Pageable pageable);

    Page<CreditEntity> findAllCredits(Pageable pageable);
}
