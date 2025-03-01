package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.nailservice.CreateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceSearchRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.UpdateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.sort.service.ServiceSortProperty;
import com.hesmantech.salonbooking.domain.GroupEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
import com.hesmantech.salonbooking.domain.model.service.ServiceStatus;
import com.hesmantech.salonbooking.exception.service.ServiceNotFoundException;
import com.hesmantech.salonbooking.repository.GroupRepository;
import com.hesmantech.salonbooking.repository.ServiceRepository;
import com.hesmantech.salonbooking.service.impl.NailServiceServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class NailServiceServiceTests {
    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private GroupRepository groupRepository;

    private NailServiceService nailServiceService;

    @BeforeEach
    void setUp() {
        this.nailServiceService = new NailServiceServiceImpl(
                serviceRepository,
                groupRepository
        );
    }

    @Test
    void testGetDetailsFailed() {
        try {
            // given
            var serviceId = UUID.randomUUID();

            Mockito.when(serviceRepository.findById(serviceId))
                    .thenReturn(Optional.empty());

            // when
            nailServiceService.getDetails(serviceId);
        } catch (ServiceNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Test
    void testGetDetailsSuccess() {
        // given
        var serviceId = UUID.randomUUID();

        var exampleServiceEntity = new ServiceEntity();
        exampleServiceEntity.setId(serviceId);

        Mockito.when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.of(exampleServiceEntity));

        // when
        var result = nailServiceService.getDetails(serviceId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(serviceId);
    }

    @Test
    void testDeleteFailed() {
        try {
            // given
            var serviceId = UUID.randomUUID();

            Mockito.when(serviceRepository.findById(serviceId))
                    .thenReturn(Optional.empty());

            // when
            nailServiceService.delete(serviceId);
        } catch (ServiceNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Test
    void testDeleteSuccess() {
        // given
        var serviceId = UUID.randomUUID();

        var exampleServiceEntity = new ServiceEntity();
        exampleServiceEntity.setId(serviceId);

        Mockito.when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.of(exampleServiceEntity));

        Mockito.when(serviceRepository.save(Mockito.any(ServiceEntity.class)))
                .thenReturn(exampleServiceEntity);

        // when
        var result = nailServiceService.delete(serviceId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(serviceId);
        Assertions.assertThat(result.getStatus()).isEqualTo(ServiceStatus.DELETED);
    }

    @Test
    void testCreateSuccess() {
        // given
        var startPrice = 10d;
        var endPrice = 20d;
        var priceType = ServicePriceType.IN_RANGE;
        var groupId = UUID.randomUUID();
        var request = new CreateNailServiceRequest(
                "Test service",
                startPrice,
                endPrice,
                priceType,
                groupId
        );

        var exampleGroupEntity = GroupEntity.builder()
                .id(groupId)
                .build();
        Mockito.when(groupRepository.findById(groupId))
                .thenReturn(Optional.of(exampleGroupEntity));

        var exampleServiceEntity = ServiceEntity.builder()
                .name(request.name())
                .group(exampleGroupEntity)
                .startPrice(startPrice)
                .endPrice(endPrice)
                .servicePriceType(priceType)
                .status(ServiceStatus.ACTIVE)
                .build();
        Mockito.when(serviceRepository.save(Mockito.any(ServiceEntity.class)))
                .thenReturn(exampleServiceEntity);

        // when
        var result = nailServiceService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(exampleServiceEntity.getName());
        Assertions.assertThat(result.getStartPrice()).isEqualTo(startPrice);
        Assertions.assertThat(result.getEndPrice()).isEqualTo(endPrice);
        Assertions.assertThat(result.getServicePriceType()).isEqualTo(priceType);
        Assertions.assertThat(result.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        Assertions.assertThat(result.getGroup()).isNotNull();

        var group = result.getGroup();
        Assertions.assertThat(group.getId()).isEqualTo(groupId);
    }

    @Test
    void testCreateEndPriceLessThanStartPriceSuccess() {
        // given
        var startPrice = 20d;
        var endPrice = 10d;
        var priceType = ServicePriceType.IN_RANGE;
        var groupId = UUID.randomUUID();
        var request = new CreateNailServiceRequest(
                "Test service",
                startPrice,
                endPrice,
                priceType,
                groupId
        );

        var exampleGroupEntity = GroupEntity.builder()
                .id(groupId)
                .build();
        Mockito.when(groupRepository.findById(groupId))
                .thenReturn(Optional.of(exampleGroupEntity));

        var exampleServiceEntity = ServiceEntity.builder()
                .name(request.name())
                .group(exampleGroupEntity)
                .startPrice(startPrice)
                .endPrice(endPrice)
                .servicePriceType(priceType)
                .status(ServiceStatus.ACTIVE)
                .build();
        Mockito.when(serviceRepository.save(Mockito.any(ServiceEntity.class)))
                .thenReturn(exampleServiceEntity);

        // when
        var result = nailServiceService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(exampleServiceEntity.getName());
        Assertions.assertThat(result.getStartPrice()).isEqualTo(startPrice);
        Assertions.assertThat(result.getEndPrice()).isEqualTo(endPrice);
        Assertions.assertThat(result.getServicePriceType()).isEqualTo(priceType);
        Assertions.assertThat(result.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        Assertions.assertThat(result.getGroup()).isNotNull();

        var group = result.getGroup();
        Assertions.assertThat(group.getId()).isEqualTo(groupId);
    }

    @Test
    void testCreateEndPriceNullSuccess() {
        // given
        var startPrice = 10d;
        var priceType = ServicePriceType.START_PRICE_AND_ABOVE;
        var groupId = UUID.randomUUID();
        var request = new CreateNailServiceRequest(
                "Test service",
                startPrice,
                null,
                priceType,
                groupId
        );

        var exampleGroupEntity = GroupEntity.builder()
                .id(groupId)
                .build();
        Mockito.when(groupRepository.findById(groupId))
                .thenReturn(Optional.of(exampleGroupEntity));

        var exampleServiceEntity = ServiceEntity.builder()
                .name(request.name())
                .group(exampleGroupEntity)
                .startPrice(startPrice)
                .servicePriceType(priceType)
                .status(ServiceStatus.ACTIVE)
                .build();
        Mockito.when(serviceRepository.save(Mockito.any(ServiceEntity.class)))
                .thenReturn(exampleServiceEntity);

        // when
        var result = nailServiceService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(exampleServiceEntity.getName());
        Assertions.assertThat(result.getStartPrice()).isEqualTo(startPrice);
        Assertions.assertThat(result.getEndPrice()).isNull();
        Assertions.assertThat(result.getServicePriceType()).isEqualTo(priceType);
        Assertions.assertThat(result.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        Assertions.assertThat(result.getGroup()).isNotNull();

        var group = result.getGroup();
        Assertions.assertThat(group.getId()).isEqualTo(groupId);
    }

    @Test
    void testCreateGroupEmptySuccess() {
        // given
        var startPrice = 10d;
        var endPrice = 20d;
        var priceType = ServicePriceType.IN_RANGE;
        var request = new CreateNailServiceRequest(
                "Test service",
                startPrice,
                endPrice,
                priceType,
                null
        );

        var exampleServiceEntity = ServiceEntity.builder()
                .name(request.name())
                .startPrice(startPrice)
                .endPrice(endPrice)
                .servicePriceType(priceType)
                .status(ServiceStatus.ACTIVE)
                .build();
        Mockito.when(serviceRepository.save(Mockito.any(ServiceEntity.class)))
                .thenReturn(exampleServiceEntity);

        // when
        var result = nailServiceService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(exampleServiceEntity.getName());
        Assertions.assertThat(result.getStartPrice()).isEqualTo(startPrice);
        Assertions.assertThat(result.getEndPrice()).isEqualTo(endPrice);
        Assertions.assertThat(result.getServicePriceType()).isEqualTo(priceType);
        Assertions.assertThat(result.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        Assertions.assertThat(result.getGroup()).isNull();
    }

    @Test
    void testUpdateFailed() {
        try {
            // given
            var serviceId = UUID.randomUUID();
            var request = new UpdateNailServiceRequest(
                    null,
                    null,
                    null,
                    null,
                    null
            );

            Mockito.when(serviceRepository.findById(serviceId))
                    .thenReturn(Optional.empty());

            // when
            nailServiceService.update(serviceId, request);
        } catch (ServiceNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(ServiceNotFoundException.class);
        }
    }

    @Test
    void testUpdateSuccess() {
        // given
        var serviceId = UUID.randomUUID();
        var groupId = UUID.randomUUID();
        var request = new UpdateNailServiceRequest(
                "New service name",
                15d,
                25d,
                ServicePriceType.IN_RANGE,
                groupId
        );

        var exampleGroupEntity = new GroupEntity();
        exampleGroupEntity.setId(groupId);
        Mockito.when(groupRepository.findById(groupId))
                .thenReturn(Optional.of(exampleGroupEntity));

        var exampleServiceEntity = ServiceEntity.builder()
                .id(serviceId)
                .name("Old service name")
                .startPrice(10d)
                .endPrice(20d)
                .servicePriceType(ServicePriceType.IN_RANGE)
                .group(new GroupEntity())
                .build();
        Mockito.when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.of(exampleServiceEntity));

        Mockito.when(serviceRepository.save(Mockito.any(ServiceEntity.class)))
                .thenReturn(exampleServiceEntity);

        // when
        var result = nailServiceService.update(serviceId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(request.name());
        Assertions.assertThat(result.getStartPrice()).isEqualTo(request.startPrice());
        Assertions.assertThat(result.getEndPrice()).isEqualTo(request.endPrice());
        Assertions.assertThat(result.getGroup()).isNotNull();
        var group = result.getGroup();
        Assertions.assertThat(group.getId()).isEqualTo(groupId);
    }

    @Test
    void testSearchSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = ServiceSortProperty.CREATED_DATE;
        var request = new NailServiceSearchRequest(
                "Test service",
                null
        );

        Mockito.when(serviceRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));

        // when
        var result = nailServiceService.search(page, size, direction, property, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isEmpty();
        Assertions.assertThat(result.getPageable()).isNotNull();
        var pageable = result.getPageable();
        Assertions.assertThat(pageable.getPageNumber()).isEqualTo(page);
        Assertions.assertThat(pageable.getPageSize()).isEqualTo(size);
    }
}
