package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.OrderResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.order.OrderResponse;
import com.hesmantech.salonbooking.api.dto.order.OrderUpdateRequest;
import com.hesmantech.salonbooking.api.dto.order.SearchOrderRequest;
import com.hesmantech.salonbooking.api.dto.sort.order.OrderSortProperty;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.mapper.NailServiceMapper;
import com.hesmantech.salonbooking.mapper.OrderMapper;
import com.hesmantech.salonbooking.mapper.UserMapper;
import com.hesmantech.salonbooking.service.OrderService;
import com.hesmantech.salonbooking.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderResourceImpl implements OrderResource {
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final NailServiceMapper nailServiceMapper;
    private final ReportService reportService;

    @PostMapping("/search")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<OrderResponse> search(int page, int size, Sort.Direction direction, OrderSortProperty property, SearchOrderRequest searchOrderRequest, Principal principal) {
        try {
            Page<OrderResponse> pageOrderResponse = orderService.search(page, size, direction, property, searchOrderRequest)
                    .map(this::mapToOrderResponse);

            log.info("Search orders successfully from {}", principal.getName());

            return PageResponse.<OrderResponse>builder()
                    .content(pageOrderResponse.getContent())
                    .page(pageOrderResponse.getPageable().getPageNumber())
                    .size(pageOrderResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageOrderResponse.getTotalPages())
                    .totalElements(pageOrderResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search all orders: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public OrderResponse getOrderDetails(@PathVariable UUID id, Principal principal) {
        try {
            OrderResponse orderResponse = mapToOrderResponse(orderService.getOrderDetails(id));

            log.info("Get order details successfully from {}", principal.getName());

            return orderResponse;
        } catch (Exception e) {
            log.error("Failed to get order details: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/assign-technician/{technicianId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public OrderResponse assignTechnician(@PathVariable UUID id, @PathVariable UUID technicianId, Principal principal) {
        try {
            OrderResponse orderResponse = mapToOrderResponse(orderService.assignTechnician(id, technicianId));

            log.info("Assign technician {} to order {} successfully from {}", technicianId, id, principal.getName());

            return orderResponse;
        } catch (Exception e) {
            log.error("Failed to assign technician: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public OrderResponse cancel(@PathVariable UUID id, Principal principal) {
        try {
            OrderResponse orderResponse = orderMapper.toOrderResponse(
                    orderService.cancel(id));

            log.info("Cancel order {} successfully from: {}", id, principal.getName());

            return orderResponse;
        } catch (Exception e) {
            log.error("Failed to cancel order: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public OrderResponse update(@PathVariable UUID id, OrderUpdateRequest updateRequest, Principal principal) {
        try {
            OrderResponse orderResponse = mapToOrderResponse(orderService.update(id, updateRequest));

            log.info("Update order {} successfully from {}", id, principal.getName());

            return orderResponse;
        } catch (Exception e) {
            log.error("Failed to update order: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public ResponseEntity<byte[]> report(SearchOrderRequest request, Principal principal) {
        try {
            var data = orderService.prepareReportData(request);
            var report = reportService.generateOrderReport(data);

            log.info("Report order successfully from {}", principal.getName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.fileName())
                    .body(report.content());
        } catch (Exception e) {
            log.error("Failed to report order: {}", e.getMessage(), e);
            throw e;
        }
    }

    private OrderResponse mapToOrderResponse(OrderEntity orderEntity) {
        return orderMapper.toOrderResponse(orderEntity)
                .withNailTechnician(userMapper.toNailTechnicianResponse(orderEntity))
                .withServices(nailServiceMapper.toNailServiceResponseList(orderEntity));
    }
}
