package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
import com.hesmantech.salonbooking.mapper.impl.NailServiceMapperImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class NailServiceMapperTests {
    private NailServiceMapper nailServiceMapper;

    @BeforeEach
    void setUp() {
        this.nailServiceMapper = new NailServiceMapperImpl();
    }

    @Test
    void testToNailServiceResponseSuccess() {
        // given
        var serviceEntity = new ServiceEntity();
        serviceEntity.setId(UUID.randomUUID());
        serviceEntity.setStartPrice(10d);
        serviceEntity.setEndPrice(20d);
        serviceEntity.setServicePriceType(ServicePriceType.IN_RANGE);

        // when
        var result = nailServiceMapper.toNailServiceResponse(serviceEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(serviceEntity.getId().toString());
        Assertions.assertThat(result.price())
                .contains(serviceEntity.getStartPrice().toString(),
                        serviceEntity.getEndPrice().toString(),
                        "-");
    }

    @Test
    void testToNailServiceResponsePriceAboveSuccess() {
        // given
        var serviceEntity = new ServiceEntity();
        serviceEntity.setId(UUID.randomUUID());
        serviceEntity.setStartPrice(10d);
        serviceEntity.setServicePriceType(ServicePriceType.START_PRICE_AND_ABOVE);

        // when
        var result = nailServiceMapper.toNailServiceResponse(serviceEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(serviceEntity.getId().toString());
        Assertions.assertThat(result.price())
                .contains(serviceEntity.getStartPrice().toString(),
                        "+");
    }

    @Test
    void testToNailServiceResponseStartPriceOnlySuccess() {
        // given
        var serviceEntity = new ServiceEntity();
        serviceEntity.setId(UUID.randomUUID());
        serviceEntity.setStartPrice(10d);
        serviceEntity.setServicePriceType(ServicePriceType.START_PRICE_ONLY);

        // when
        var result = nailServiceMapper.toNailServiceResponse(serviceEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(serviceEntity.getId().toString());
        Assertions.assertThat(result.price()).contains(serviceEntity.getStartPrice().toString());
    }

    @Test
    void testToNailServiceResponseFromOrderedSuccess() {
        // given
        var orderedDetailsEntity = new OrderedDetailsEntity();
        orderedDetailsEntity.setServiceStartPrice(10d);
        orderedDetailsEntity.setServicePriceType(ServicePriceType.START_PRICE_ONLY);

        // when
        var result = nailServiceMapper.toNailServiceResponse(orderedDetailsEntity);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testToNailServiceResponseListSuccess() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CHECK_OUT);
        var orderedDetailsEntity = new OrderedDetailsEntity();
        orderedDetailsEntity.setServiceStartPrice(10d);
        orderedDetailsEntity.setServicePriceType(ServicePriceType.START_PRICE_ONLY);
        orderEntity.setOrderedDetails(List.of(orderedDetailsEntity));

        // when
        var result = nailServiceMapper.toNailServiceResponseList(orderEntity);

        // then
        Assertions.assertThat(result).isNotEmpty();
        result.forEach(nailService -> {
            Assertions.assertThat(nailService.price()).contains(orderedDetailsEntity.getServiceStartPrice().toString());
        });
    }

    @Test
    void testToNailServiceResponseListEmptySuccess() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CHECK_OUT);

        // when
        var result = nailServiceMapper.toNailServiceResponseList(orderEntity);

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void testToNailServiceResponseListEmpty2Success() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CHECK_OUT);
        orderEntity.setOrderedDetails(List.of());

        // when
        var result = nailServiceMapper.toNailServiceResponseList(orderEntity);

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void testToNailServiceResponseListInServiceSuccess() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.IN_SERVICE);
        var orderDetailsEntity = new OrderDetailsEntity();
        var serviceEntity = ServiceEntity.builder()
                .id(UUID.randomUUID())
                .startPrice(10d)
                .servicePriceType(ServicePriceType.START_PRICE_ONLY)
                .build();
        orderDetailsEntity.setService(serviceEntity);
        orderEntity.setOrderDetails(List.of(orderDetailsEntity));

        // when
        var result = nailServiceMapper.toNailServiceResponseList(orderEntity);

        // then
        Assertions.assertThat(result).isNotEmpty();
        result.forEach(nailService -> {
            Assertions.assertThat(nailService.id()).isEqualTo(serviceEntity.getId().toString());
            Assertions.assertThat(nailService.price()).contains(serviceEntity.getStartPrice().toString());
        });
    }

    @Test
    void testToNailServiceDetailsResponseSuccess() {
        // when
        var result = nailServiceMapper.toNailServiceDetailsResponse(new ServiceEntity());

        // then
        Assertions.assertThat(result).isNotNull();
    }
}
