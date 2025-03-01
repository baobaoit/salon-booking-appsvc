package com.hesmantech.salonbooking.mapper.impl;

import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceDetailsResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceResponse;
import com.hesmantech.salonbooking.domain.GroupEntity;
import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
import com.hesmantech.salonbooking.mapper.NailServiceMapper;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.hesmantech.salonbooking.constants.Constants.NA;

@Component
public class NailServiceMapperImpl implements NailServiceMapper {
    @Override
    public NailServiceResponse toNailServiceResponse(ServiceEntity entity) {
        final String id = entity.getId().toString();
        final String name = entity.getName();

        final String price = generatePrice(entity.getStartPrice(), entity.getEndPrice(), entity.getServicePriceType());

        final String group = Optional.ofNullable(entity.getGroup())
                .map(GroupEntity::getName).orElse(NA);

        final String createdDate = InstantMapper.instantToString(entity.getCreatedDate());

        return new NailServiceResponse(id, name, price, group, createdDate);
    }

    @Override
    public NailServiceResponse toNailServiceResponse(OrderedDetailsEntity orderedDetailsEntity) {
        final String serviceId = String.valueOf(orderedDetailsEntity.getServiceId());
        final String serviceName = orderedDetailsEntity.getServiceName();
        final String serviceGroup = orderedDetailsEntity.getServiceGroupName();
        final String price = generatePrice(orderedDetailsEntity.getServiceStartPrice(),
                orderedDetailsEntity.getServiceEndPrice(), orderedDetailsEntity.getServicePriceType());

        return new NailServiceResponse(serviceId, serviceName, price, serviceGroup, "");
    }

    @Override
    public List<NailServiceResponse> toNailServiceResponseList(OrderEntity orderEntity) {
        OrderStatus orderStatus = orderEntity.getStatus();
        boolean isOrderCheckedOut = OrderStatus.CHECK_OUT.equals(orderStatus);

        List<? extends AbstractAuditEntity> orderDetails = isOrderCheckedOut ?
                orderEntity.getOrderedDetails() : orderEntity.getOrderDetails();

        if (CollectionUtils.isEmpty(orderDetails)) {
            return List.of();
        }

        return orderDetails.stream()
                .map(od -> isOrderCheckedOut ?
                        toNailServiceResponse((OrderedDetailsEntity) od) :
                        toNailServiceResponse(((OrderDetailsEntity) od).getService()))
                .toList();
    }

    @Override
    public NailServiceDetailsResponse toNailServiceDetailsResponse(ServiceEntity entity) {
        return new NailServiceDetailsResponse(
                String.valueOf(entity.getId()),
                entity.getName(),
                null,
                entity.getStatus(),
                entity.getStartPrice(),
                entity.getEndPrice(),
                entity.getServicePriceType(),
                InstantMapper.instantToString(entity.getCreatedDate()));
    }

    private String generatePrice(Double startPrice, Double endPrice, ServicePriceType servicePriceType) {
        String price = "$" + startPrice;

        if (List.of(ServicePriceType.NONE, ServicePriceType.START_PRICE_ONLY).contains(servicePriceType)) {
            return price;
        }

        if (ServicePriceType.START_PRICE_AND_ABOVE.equals(servicePriceType)) {
            price += "+";
        } else {
            price += "-$" + endPrice;
        }

        return price;
    }
}
