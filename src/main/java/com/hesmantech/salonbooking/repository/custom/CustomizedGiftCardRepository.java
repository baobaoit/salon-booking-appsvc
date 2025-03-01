package com.hesmantech.salonbooking.repository.custom;

import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomizedGiftCardRepository {
    Page<GiftCardEntity> findAllGiftCards(Predicate predicate, Pageable pageable);

    List<GiftCardEntity> findAllGiftCards(Predicate predicate);
}
