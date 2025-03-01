package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditRequest;
import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface CreditService {
    <T extends AbstractAuditEntity> void updateBasedOn(T entity);

    Page<CreditEntity> search(int page, int size, Sort.Direction direction, CustomerCreditSortProperty property,
                              SearchCustomerCreditRequest request);

    CreditEntity getDetails(UUID customerId);
}
