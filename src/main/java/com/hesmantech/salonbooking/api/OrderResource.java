package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.order.OrderResponse;
import com.hesmantech.salonbooking.api.dto.order.OrderUpdateRequest;
import com.hesmantech.salonbooking.api.dto.order.SearchOrderRequest;
import com.hesmantech.salonbooking.api.dto.sort.order.OrderSortProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Order Resource")
@Validated
public interface OrderResource {
    PageResponse<OrderResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                       @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
                                       @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                       @RequestParam(required = false, defaultValue = "CREATED_DATE") OrderSortProperty property,
                                       @RequestBody SearchOrderRequest searchOrderRequest,
                                       Principal principal);

    OrderResponse getOrderDetails(UUID id, Principal principal);

    OrderResponse assignTechnician(UUID id, UUID technicianId, Principal principal);

    OrderResponse cancel(UUID id, Principal principal);

    OrderResponse update(UUID id, @RequestBody OrderUpdateRequest updateRequest, Principal principal);

    ResponseEntity<byte[]> report(@RequestBody SearchOrderRequest request, Principal principal);
}
