package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckInRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckOutRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerRegistrationRequest;
import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.exception.order.OrderAlreadyCheckedOutException;
import com.hesmantech.salonbooking.exception.order.OrderIsWaitingServiceException;
import com.hesmantech.salonbooking.exception.order.OrderNotFoundException;
import com.hesmantech.salonbooking.exception.role.RoleNotFoundException;
import com.hesmantech.salonbooking.helper.order.OrderHelper;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.repository.OrderedDetailsRepository;
import com.hesmantech.salonbooking.repository.RoleRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.service.impl.CustomerServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderedDetailsRepository orderedDetailsRepository;

    @Mock
    private OrderHelper orderHelper;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        this.customerService = new CustomerServiceImpl(
                roleRepository,
                userRepository,
                orderRepository,
                orderedDetailsRepository,
                orderHelper
        );
    }

    @Test
    void testCreateFailed() {
        try {
            // given
            Mockito.when(roleRepository.findById(UserRole.ROLE_CUSTOMER.id()))
                    .thenReturn(Optional.empty());

            // when
            customerService.create(null);
        } catch (RoleNotFoundException e) {
            Assertions.assertThat(e).isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Test
    void testCreateSuccess() {
        // given
        var request = new CustomerRegistrationRequest(
                "First name",
                "Last name",
                null,
                null,
                null,
                null
        );
        var exampleRoleEntity = new RoleEntity();
        exampleRoleEntity.setId(UserRole.ROLE_CUSTOMER.id());

        Mockito.when(roleRepository.findById(UserRole.ROLE_CUSTOMER.id()))
                .thenReturn(Optional.of(exampleRoleEntity));

        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(UserEntity.builder()
                        .firstName("First name")
                        .lastName("Last name")
                        .role(exampleRoleEntity)
                        .build());

        // when
        var result = customerService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFirstName()).isEqualTo(request.firstName());
        Assertions.assertThat(result.getLastName()).isEqualTo(request.lastName());
        Assertions.assertThat(result.getRole()).isNotNull();
        var role = result.getRole();
        Assertions.assertThat(role.getId()).isEqualTo(UserRole.ROLE_CUSTOMER.id());
    }

    @Test
    void testCustomerCheckInSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var request = new CustomerCheckInRequest(UUID.randomUUID(), "", Set.of());

        Mockito.when(orderHelper.findCustomerAndCreateOrder(
                        customerId,
                        request.clientNotes(),
                        request.technicianId(),
                        request.services()))
                .thenReturn(new OrderEntity());

        // when
        var result = customerService.checkIn(customerId, request);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testCustomerCheeckOutFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var orderId = UUID.randomUUID();
            var request = new CustomerCheckOutRequest(
                    orderId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            Mockito.when(orderRepository.findByIdAndCustomer_Id(orderId, customerId))
                    .thenReturn(Optional.empty());

            // when
            customerService.checkOut(customerId, request);
        } catch (OrderNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderNotFoundException.class);
        }
    }

    @Test
    void testCustomerCheeckOutOrderHasStatusWaitingServiceFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var orderId = UUID.randomUUID();
            var request = new CustomerCheckOutRequest(
                    orderId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            Mockito.when(orderRepository.findByIdAndCustomer_Id(orderId, customerId))
                    .thenReturn(Optional.of(OrderEntity.builder()
                            .status(OrderStatus.WAITING_SERVICE)
                            .build()));

            // when
            customerService.checkOut(customerId, request);
        } catch (OrderIsWaitingServiceException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderIsWaitingServiceException.class);
        }
    }

    @Test
    void testCustomerCheeckOutOrderHasStatusCheckOutFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var orderId = UUID.randomUUID();
            var request = new CustomerCheckOutRequest(
                    orderId,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            Mockito.when(orderRepository.findByIdAndCustomer_Id(orderId, customerId))
                    .thenReturn(Optional.of(OrderEntity.builder()
                            .status(OrderStatus.CHECK_OUT)
                            .build()));

            // when
            customerService.checkOut(customerId, request);
        } catch (OrderAlreadyCheckedOutException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderAlreadyCheckedOutException.class);
        }
    }

    @Test
    void testCustomerCheeckOutSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var request = new CustomerCheckOutRequest(
                orderId,
                UUID.randomUUID(),
                null,
                null,
                null,
                null,
                null,
                null
        );

        var exampleOrderEntity = OrderEntity.builder()
                .id(orderId)
                .status(OrderStatus.IN_SERVICE)
                .employee(new UserEntity())
                .orderDetails(List.of(OrderDetailsEntity.builder()
                        .service(new ServiceEntity())
                        .build()))
                .orderedDetails(new ArrayList<>())
                .build();
        Mockito.when(orderRepository.findByIdAndCustomer_Id(orderId, customerId))
                .thenReturn(Optional.of(exampleOrderEntity));

        Mockito.when(orderHelper.assignTechnician(exampleOrderEntity, request.technicianId()))
                .thenReturn(exampleOrderEntity);

        Mockito.when(orderHelper.assignServices(exampleOrderEntity, request.services()))
                .thenReturn(exampleOrderEntity);

        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntity);

        // when
        var result = customerService.checkOut(customerId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.CHECK_OUT);
        Assertions.assertThat(result.getOrderedDetails()).isNotEmpty();
    }

    @Test
    void testPrepareReportDataSuccess() {
        // given
        Mockito.when(userRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(new UserEntity()));

        // when
        var result = customerService.prepareReportData();

        // then
        Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(1);
    }
}
