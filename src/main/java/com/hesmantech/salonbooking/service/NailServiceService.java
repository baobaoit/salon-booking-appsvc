package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.nailservice.CreateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceSearchRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.UpdateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.sort.service.ServiceSortProperty;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface NailServiceService {
    Page<ServiceEntity> search(int page, int size, Sort.Direction direction, ServiceSortProperty property,
                               NailServiceSearchRequest searchRequest);

    ServiceEntity create(CreateNailServiceRequest createNailServiceRequest);

    ServiceEntity update(UUID id, UpdateNailServiceRequest updateNailServiceRequest);

    ServiceEntity getDetails(UUID id);

    ServiceEntity delete(UUID id);
}
