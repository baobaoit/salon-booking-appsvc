package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.nailservice.CreateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceSearchRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.UpdateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.sort.service.ServiceSortProperty;
import com.hesmantech.salonbooking.domain.GroupEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.model.service.ServiceStatus;
import com.hesmantech.salonbooking.exception.service.ServiceNotFoundException;
import com.hesmantech.salonbooking.repository.GroupRepository;
import com.hesmantech.salonbooking.repository.ServiceRepository;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.NailServiceParamsBuilder;
import com.hesmantech.salonbooking.service.NailServiceService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NailServiceServiceImpl implements NailServiceService {
    private final ServiceRepository serviceRepository;
    private final GroupRepository groupRepository;

    @Override
    public Page<ServiceEntity> search(int page, int size, Sort.Direction direction, ServiceSortProperty property,
                                      NailServiceSearchRequest searchRequest) {
        final NailServiceParamsBuilder serviceParamsBuilder = NailServiceParamsBuilder.from(page, size, direction,
                property, searchRequest);
        final BooleanExpression criteria = serviceParamsBuilder.getCommonCriteriaValue();
        final Pageable pageable = serviceParamsBuilder.getPageable();

        log.info("Search Nail service with criteria: {}", criteria);

        return serviceRepository.findAll(criteria, pageable);
    }

    @Override
    public ServiceEntity create(CreateNailServiceRequest createNailServiceRequest) {
        Double startPrice = createNailServiceRequest.startPrice();
        Double endPrice = createNailServiceRequest.endPrice();

        if (endPrice != null && startPrice.compareTo(endPrice) < 0) {
            Double tmpPrice = startPrice;
            startPrice = endPrice;
            endPrice = tmpPrice;
        }

        GroupEntity groupEntity = Optional.ofNullable(createNailServiceRequest.groupId())
                .flatMap(groupRepository::findById)
                .orElse(null);

        return serviceRepository.save(ServiceEntity.builder()
                .name(createNailServiceRequest.name())
                .group(groupEntity)
                .startPrice(startPrice)
                .endPrice(endPrice)
                .servicePriceType(createNailServiceRequest.priceType())
                .status(ServiceStatus.ACTIVE)
                .build());
    }

    @Override
    public ServiceEntity update(UUID id, UpdateNailServiceRequest updateNailServiceRequest) {
        GroupEntity groupEntity = Optional.ofNullable(updateNailServiceRequest.groupId())
                .flatMap(groupRepository::findById)
                .orElse(null);

        return serviceRepository.findById(id)
                .map(service -> {
                    service.setName(updateNailServiceRequest.name());
                    service.setStartPrice(updateNailServiceRequest.startPrice());
                    service.setEndPrice(updateNailServiceRequest.endPrice());
                    service.setServicePriceType(updateNailServiceRequest.priceType());
                    service.setGroup(groupEntity);

                    return serviceRepository.save(service);
                })
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }

    @Override
    public ServiceEntity getDetails(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }

    @Override
    public ServiceEntity delete(UUID id) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setStatus(ServiceStatus.DELETED);

                    return serviceRepository.save(service);
                })
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }
}
