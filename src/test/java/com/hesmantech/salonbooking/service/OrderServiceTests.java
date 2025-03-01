package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.order.OrderUpdateRequest;
import com.hesmantech.salonbooking.api.dto.order.SearchOrderRequest;
import com.hesmantech.salonbooking.api.dto.sort.order.OrderSortProperty;
import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.exception.order.OrderAlreadyCheckedOutException;
import com.hesmantech.salonbooking.exception.order.OrderCannotCancelException;
import com.hesmantech.salonbooking.exception.order.OrderNotFoundException;
import com.hesmantech.salonbooking.helper.order.OrderHelper;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.service.impl.OrderServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderHelper orderHelper;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderServiceImpl(
                orderRepository,
                orderHelper
        );
    }

    @Test
    void testGetOrderDetailsFailed() {
        try {
            // given
            Mockito.when(orderRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            orderService.getOrderDetails(UUID.randomUUID());
        } catch (OrderNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderNotFoundException.class);
        }
    }

    @Test
    void testGetOrderDetailsSuccess() {
        // given
        var orderId = UUID.randomUUID();
        var exampleOrderEntity = new OrderEntity();
        exampleOrderEntity.setId(orderId);

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(exampleOrderEntity));

        // when
        var result = orderService.getOrderDetails(orderId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(orderId);
    }

    @Test
    void testAssignTechnicianSuccess() {
        // given
        var orderId = UUID.randomUUID();
        var technicianId = UUID.randomUUID();
        var exampleOrderEntity = OrderEntity.builder()
                .id(orderId)
                .employee(UserEntity.builder()
                        .id(technicianId)
                        .role(RoleEntity.builder()
                                .id(UserRole.ROLE_TECHNICIAN.id())
                                .build())
                        .build())
                .build();

        Mockito.when(orderHelper.assignTechnician(orderId, technicianId))
                .thenReturn(exampleOrderEntity);

        // when
        var result = orderService.assignTechnician(orderId, technicianId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(orderId);
        Assertions.assertThat(result.getEmployee()).isNotNull();

        var technician = result.getEmployee();
        Assertions.assertThat(technician.getId()).isEqualTo(technicianId);
        Assertions.assertThat(technician.getRole().getId()).isEqualTo(UserRole.ROLE_TECHNICIAN.id());
    }

    @Test
    void testCancelOrderFailed() {
        try {
            // given
            var orderId = UUID.randomUUID();

            Mockito.when(orderRepository.findByIdAndStatusIn(orderId, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE)))
                    .thenReturn(Optional.empty());

            // when
            orderService.cancel(orderId);
        } catch (OrderCannotCancelException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderCannotCancelException.class);
        }
    }

    @Test
    void testCancelOrderSuccess() {
        // given
        var orderId = UUID.randomUUID();
        var exampleOrderEntity = new OrderEntity();
        exampleOrderEntity.setId(orderId);
        exampleOrderEntity.setStatus(OrderStatus.IN_SERVICE);

        Mockito.when(orderRepository.findByIdAndStatusIn(orderId, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE)))
                .thenReturn(Optional.of(exampleOrderEntity));

        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntity);

        // when
        var result = orderService.cancel(orderId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(orderId);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    void testSearchSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = OrderSortProperty.CREATED_DATE;
        var customerId = UUID.randomUUID();
        var request = new SearchOrderRequestBuilder()
                .withCustomerId(customerId)
                .build();

        Mockito.when(orderRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));

        // when
        var result = orderService.search(page, size, direction, property, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isEmpty();

        var pageable = result.getPageable();
        Assertions.assertThat(pageable).isNotNull();
        Assertions.assertThat(pageable.getPageNumber()).isEqualTo(page);
        Assertions.assertThat(pageable.getPageSize()).isEqualTo(size);
    }

    @Test
    void testUpdateOrderFailed() {
        try {
            // given
            var orderId = UUID.randomUUID();

            Mockito.when(orderRepository.findByIdAndStatusIn(orderId, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE)))
                    .thenReturn(Optional.empty());

            // when
            orderService.update(orderId, null);
        } catch (OrderAlreadyCheckedOutException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderAlreadyCheckedOutException.class);
        }
    }

    @Test
    void testUpdateOrderSuccess() {
        // given
        var orderId = UUID.randomUUID();
        var oldTechnicianId = UUID.randomUUID();
        var newTechnicianId = UUID.randomUUID();
        var oldServiceId = UUID.randomUUID();
        var newServiceId = UUID.randomUUID();
        var newServiceSet = Set.of(newServiceId);
        var request = new OrderUpdateRequest("New client notes", newTechnicianId, newServiceSet);
        var exampleOrderEntity = OrderEntity.builder()
                .id(orderId)
                .customerNotes("Old customer notes")
                .employee(UserEntity.builder()
                        .id(oldTechnicianId)
                        .build())
                .orderDetails(List.of(OrderDetailsEntity.builder()
                        .service(ServiceEntity.builder()
                                .id(oldServiceId)
                                .build())
                        .build()))
                .build();

        Mockito.when(orderRepository.findByIdAndStatusIn(orderId, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE)))
                .thenReturn(Optional.of(exampleOrderEntity));

        Mockito.when(orderHelper.assignTechnician(orderId, newTechnicianId))
                .thenAnswer((Answer<OrderEntity>) invocationOnMock -> {
                    var newTechnician = new UserEntity();
                    newTechnician.setId(newTechnicianId);
                    exampleOrderEntity.setEmployee(newTechnician);

                    return exampleOrderEntity;
                });

        Mockito.when(orderHelper.assignServices(exampleOrderEntity, newServiceSet))
                .thenAnswer((Answer<OrderEntity>) invocationOnMock -> {
                    exampleOrderEntity.setOrderDetails(List.of(OrderDetailsEntity.builder()
                            .service(ServiceEntity.builder()
                                    .id(newServiceId)
                                    .build())
                            .build()));

                    return exampleOrderEntity;
                });

        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntity);

        // when
        var result = orderService.update(orderId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCustomerNotes()).isEqualTo(request.clientNotes());
        Assertions.assertThat(result.getEmployee()).isNotNull();
        Assertions.assertThat(result.getOrderDetails()).isNotEmpty();

        var technician = result.getEmployee();
        Assertions.assertThat(technician.getId()).isEqualTo(newTechnicianId);

        var orderDetails = result.getOrderDetails().get(0);
        Assertions.assertThat(orderDetails.getService()).isNotNull();

        var service = orderDetails.getService();
        Assertions.assertThat(service.getId()).isEqualTo(newServiceId);
    }

    @Test
    void testUpdateOrderNotChangeTechnicianAndServiceSuccess() {
        // given
        var orderId = UUID.randomUUID();
        var oldTechnicianId = UUID.randomUUID();
        var oldServiceId = UUID.randomUUID();
        var request = new OrderUpdateRequest("New client notes", null, null);
        var exampleOrderEntity = OrderEntity.builder()
                .id(orderId)
                .customerNotes("Old customer notes")
                .employee(UserEntity.builder()
                        .id(oldTechnicianId)
                        .build())
                .orderDetails(List.of(OrderDetailsEntity.builder()
                        .service(ServiceEntity.builder()
                                .id(oldServiceId)
                                .build())
                        .build()))
                .build();

        Mockito.when(orderRepository.findByIdAndStatusIn(orderId, List.of(OrderStatus.WAITING_SERVICE, OrderStatus.IN_SERVICE)))
                .thenReturn(Optional.of(exampleOrderEntity));

        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntity);

        // when
        var result = orderService.update(orderId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCustomerNotes()).isEqualTo(request.clientNotes());
        Assertions.assertThat(result.getEmployee()).isNotNull();
        Assertions.assertThat(result.getOrderDetails()).isNotEmpty();

        var technician = result.getEmployee();
        Assertions.assertThat(technician.getId()).isEqualTo(oldTechnicianId);

        var orderDetails = result.getOrderDetails().get(0);
        Assertions.assertThat(orderDetails.getService()).isNotNull();

        var service = orderDetails.getService();
        Assertions.assertThat(service.getId()).isEqualTo(oldServiceId);
    }

    @Test
    void testPrepareReportDataSuccess() {
        // given
        var request = new SearchOrderRequestBuilder().build();

        Mockito.when(orderRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(new OrderEntity()));

        // when
        var result = orderService.prepareReportData(request);

        // then
        Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(1);
    }

    private static final class SearchOrderRequestBuilder {
        private UUID customerId;

        private SearchOrderRequestBuilder withCustomerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        private SearchOrderRequest build() {
            return new SearchOrderRequest(
                    customerId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}
