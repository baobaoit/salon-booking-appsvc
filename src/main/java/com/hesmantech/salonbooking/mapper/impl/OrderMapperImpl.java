package com.hesmantech.salonbooking.mapper.impl;

import com.hesmantech.salonbooking.api.dto.order.OrderResponse;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.mapper.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderMapperImpl implements OrderMapper {
    @Override
    public OrderResponse toOrderResponse(OrderEntity order) {
        UserEntity client = order.getCustomer();
        final String clientName = client.getFirstName() + " " + client.getLastName();

        return OrderResponse.builder()
                .id(String.valueOf(order.getId()))
                .clientName(clientName)
                .customerId(String.valueOf(client.getId()))
                .clientNotes(order.getCustomerNotes())
                .creationTime(String.valueOf(order.getCreatedDate()))
                .checkOutTime(Optional.ofNullable(order.getCheckOutTime())
                        .map(String::valueOf)
                        .orElse(null))
                .status(String.valueOf(order.getStatus()))
                .totalPrice(order.getPrice())
                .discount(order.getDiscount())
                .checkOutNotes(order.getCheckOutNotes())
                .build();
    }
}
