package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.order.OrderUpdateRequest;
import com.hesmantech.salonbooking.api.dto.order.SearchOrderRequest;
import com.hesmantech.salonbooking.api.dto.sort.order.OrderSortProperty;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.service.report.PrepareReportData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface OrderService extends PrepareReportData<OrderEntity, SearchOrderRequest> {
    Page<OrderEntity> search(int page, int size, Sort.Direction direction, OrderSortProperty property,
                             SearchOrderRequest searchOrderRequest);

    OrderEntity getOrderDetails(UUID id);

    OrderEntity assignTechnician(UUID id, UUID technicianId);

    OrderEntity cancel(UUID id);

    OrderEntity update(UUID id, OrderUpdateRequest orderUpdateRequest);
}
