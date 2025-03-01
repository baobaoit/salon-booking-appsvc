package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.order.OrderUpdateRequest;
import com.hesmantech.salonbooking.api.dto.order.SearchOrderRequest;
import com.hesmantech.salonbooking.api.dto.sort.order.OrderSortProperty;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.exception.order.OrderAlreadyCheckedOutException;
import com.hesmantech.salonbooking.exception.order.OrderCannotCancelException;
import com.hesmantech.salonbooking.exception.order.OrderNotFoundException;
import com.hesmantech.salonbooking.helper.order.OrderHelper;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.OrderSearchParamsBuilder;
import com.hesmantech.salonbooking.service.OrderService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderHelper orderHelper;

    @Override
    public Page<OrderEntity> search(int page, int size, Sort.Direction direction,
                                    OrderSortProperty property, SearchOrderRequest searchOrderRequest) {

        final OrderSearchParamsBuilder searchParamsBuilder = OrderSearchParamsBuilder
                .from(page, size, direction, property, searchOrderRequest);
        final BooleanExpression criteria = searchParamsBuilder.getCommonCriteriaValue();
        final Pageable pageable = searchParamsBuilder.getPageable();

        log.info("Search Order with criteria: {}", criteria);

        return orderRepository.findAll(criteria, pageable);
    }

    @Override
    public OrderEntity getOrderDetails(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public OrderEntity assignTechnician(UUID id, UUID technicianId) {
        return orderHelper.assignTechnician(id, technicianId);
    }

    @Override
    public OrderEntity cancel(UUID id) {
        return orderRepository.findByIdAndStatusIn(id, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE))
                .map(order -> {
                    order.setStatus(OrderStatus.CANCEL);

                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new OrderCannotCancelException(id));
    }

    @Override
    public OrderEntity update(UUID id, OrderUpdateRequest orderUpdateRequest) {
        return orderRepository.findByIdAndStatusIn(id, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE))
                .map(order -> {
                    order.setCustomerNotes(orderUpdateRequest.clientNotes());

                    final UUID technicianId = orderUpdateRequest.technicianId();
                    if (technicianId != null) {
                        order = assignTechnician(id, technicianId);
                    }

                    final Set<UUID> services = orderUpdateRequest.services();
                    if (!services.isEmpty()) {
                        order = orderHelper.assignServices(order, services);
                    }

                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new OrderAlreadyCheckedOutException(id));
    }

    @Override
    public List<OrderEntity> prepareReportData(SearchOrderRequest request) {
        var size = 1;
        var direction = Sort.Direction.DESC;
        var property = OrderSortProperty.CREATED_DATE;

        var searchParamsBuilder = OrderSearchParamsBuilder.from(0, size, direction, property, request);
        var criteria = searchParamsBuilder.getCommonCriteriaValue();

        List<OrderEntity> orders = new LinkedList<>();

        orderRepository.findAll(criteria, Sort.by(direction, property.getProperty()))
                .forEach(orders::add);

        return orders;
    }
}
