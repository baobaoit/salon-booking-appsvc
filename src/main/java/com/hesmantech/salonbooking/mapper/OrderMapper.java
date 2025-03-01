package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.order.OrderResponse;
import com.hesmantech.salonbooking.domain.OrderEntity;

public interface OrderMapper {
    OrderResponse toOrderResponse(OrderEntity order);
}
