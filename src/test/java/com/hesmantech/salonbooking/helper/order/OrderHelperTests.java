package com.hesmantech.salonbooking.helper.order;

import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.order.OrderAlreadyCheckedOutException;
import com.hesmantech.salonbooking.exception.order.OrderNotFoundException;
import com.hesmantech.salonbooking.exception.user.CustomerNotFoundException;
import com.hesmantech.salonbooking.exception.user.TechnicianNotFoundException;
import com.hesmantech.salonbooking.repository.OrderDetailsRepository;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.repository.ServiceRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderHelperTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private OrderDetailsRepository orderDetailsRepository;

    private OrderHelper orderHelper;

    @BeforeEach
    void setUp() {
        this.orderHelper = new OrderHelperImpl(
                userRepository,
                orderRepository,
                serviceRepository,
                orderDetailsRepository
        );
    }

    @Test
    @DisplayName("Find customer and create order success")
    void testFindCustomerAndCreateOrderSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var customerNotes = "test customer notes";
        var technicianId = UUID.randomUUID();
        var serviceId = UUID.randomUUID();
        var serviceIds = Set.of(serviceId);

        var exampleCustomer = UserEntity.builder()
                .id(customerId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleCustomer));

        var exampleTechnician = UserEntity.builder()
                .id(technicianId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleTechnician));

        var exampleOrder = OrderEntity.builder()
                .customer(exampleCustomer)
                .employee(exampleTechnician)
                .status(OrderStatus.IN_SERVICE)
                .customerNotes(customerNotes)
                .orderDetails(new ArrayList<>())
                .build();
        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrder);

        var exampleService = ServiceEntity.builder()
                .id(serviceId)
                .build();
        Mockito.reset(serviceRepository);
        Mockito.when(serviceRepository.findAllById(serviceIds))
                .thenReturn(List.of(exampleService));

        // when
        var result = orderHelper.findCustomerAndCreateOrder(customerId, customerNotes, technicianId, serviceIds);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCustomer()).isEqualTo(exampleCustomer);
        Assertions.assertThat(result.getEmployee()).isEqualTo(exampleTechnician);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_SERVICE);
        Assertions.assertThat(result.getCustomerNotes()).isEqualTo(customerNotes);
        Assertions.assertThat(result.getOrderDetails()).isNotEmpty();
        result.getOrderDetails().forEach(orderDetail -> {
            Assertions.assertThat(orderDetail).isNotNull();
            Assertions.assertThat(orderDetail.getService()).isEqualTo(exampleService);
        });
    }

    @Test
    @DisplayName("Find customer and create order without services success")
    void testFindCustomerAndCreateOrderWithoutServicesSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var customerNotes = "test customer notes";
        var technicianId = UUID.randomUUID();
        Set<UUID> serviceIds = Set.of();

        var exampleCustomer = UserEntity.builder()
                .id(customerId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleCustomer));

        var exampleTechnician = UserEntity.builder()
                .id(technicianId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleTechnician));

        var exampleOrder = OrderEntity.builder()
                .customer(exampleCustomer)
                .employee(exampleTechnician)
                .status(OrderStatus.IN_SERVICE)
                .customerNotes(customerNotes)
                .orderDetails(new ArrayList<>())
                .build();
        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrder);

        // when
        var result = orderHelper.findCustomerAndCreateOrder(customerId, customerNotes, technicianId, serviceIds);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCustomer()).isEqualTo(exampleCustomer);
        Assertions.assertThat(result.getEmployee()).isEqualTo(exampleTechnician);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_SERVICE);
        Assertions.assertThat(result.getCustomerNotes()).isEqualTo(customerNotes);
        Assertions.assertThat(result.getOrderDetails()).isEmpty();
    }

    @Test
    @DisplayName("Find customer and create order waiting service success")
    void testFindCustomerAndCreateOrderWaitingServiceSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var customerNotes = "test customer notes";
        Set<UUID> serviceIds = Set.of();

        var exampleCustomer = UserEntity.builder()
                .id(customerId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleCustomer));

        var exampleOrder = OrderEntity.builder()
                .customer(exampleCustomer)
                .status(OrderStatus.WAITING_SERVICE)
                .customerNotes(customerNotes)
                .orderDetails(new ArrayList<>())
                .build();
        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrder);

        // when
        var result = orderHelper.findCustomerAndCreateOrder(customerId, customerNotes, null, serviceIds);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCustomer()).isEqualTo(exampleCustomer);
        Assertions.assertThat(result.getEmployee()).isNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING_SERVICE);
        Assertions.assertThat(result.getCustomerNotes()).isEqualTo(customerNotes);
        Assertions.assertThat(result.getOrderDetails()).isEmpty();
    }

    @Test
    @DisplayName("Find customer and create order but technician not found")
    void testFindCustomerAndCreateOrderButTechnicianNotFound() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var customerNotes = "test customer notes";
            Set<UUID> serviceIds = Set.of();

            var exampleCustomer = UserEntity.builder()
                    .id(customerId)
                    .build();
            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.of(exampleCustomer));

            var invalidTechnicianId = UUID.randomUUID();
            Mockito.when(userRepository.findByIdAndStatus(invalidTechnicianId, UserStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            // when
            orderHelper.findCustomerAndCreateOrder(customerId, customerNotes, invalidTechnicianId, serviceIds);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(TechnicianNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Find customer and create order but customer not found")
    void testFindCustomerAndCreateOrderButCustomerNotFound() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var customerNotes = "test customer notes";
            var technicianId = UUID.randomUUID();
            Set<UUID> serviceIds = Set.of();

            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            // when
            orderHelper.findCustomerAndCreateOrder(customerId, customerNotes, technicianId, serviceIds);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Efficiently allocate services to the order")
    void testAssignServicesSuccess() {
        // given
        var exampleOrder = OrderEntity.builder()
                .orderDetails(new ArrayList<>())
                .build();
        var serviceId = UUID.randomUUID();
        var services = Set.of(serviceId);

        var exampleService = ServiceEntity.builder()
                .id(serviceId)
                .build();
        Mockito.when(serviceRepository.findAllById(Mockito.any()))
                .thenReturn(List.of(exampleService));

        // when
        var result = orderHelper.assignServices(exampleOrder, services);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getOrderDetails()).isNotEmpty();
        result.getOrderDetails().forEach(orderDetail -> {
            Assertions.assertThat(orderDetail).isNotNull();
            Assertions.assertThat(orderDetail.getService()).isEqualTo(exampleService);
        });
    }

    @Test
    @DisplayName("Successfully add an empty service list to the order")
    void testAssignEmptyServicesSuccess() {
        // given
        var exampleOrder = OrderEntity.builder()
                .orderDetails(new ArrayList<>())
                .build();
        Set<UUID> services = Set.of();

        // when
        var result = orderHelper.assignServices(exampleOrder, services);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getOrderDetails()).isEmpty();
    }

    @Test
    @DisplayName("Successfully updated the service list for the order")
    void testModifyServicesSuccess() {
        // given
        var oldServiceId = UUID.randomUUID();
        var oldServiceIdRemoved = UUID.randomUUID();
        var newServiceId = UUID.randomUUID();
        var services = Set.of(oldServiceId, newServiceId);

        var exampleOldService = ServiceEntity.builder()
                .id(oldServiceId)
                .build();
        var exampleOldServiceRemoved = ServiceEntity.builder()
                .id(oldServiceIdRemoved)
                .build();
        var exampleOrder = OrderEntity.builder()
                .orderDetails(new ArrayList<>())
                .build();
        exampleOrder.addOrderDetails(OrderDetailsEntity.builder()
                .id(UUID.randomUUID())
                .service(exampleOldService)
                .build());
        exampleOrder.addOrderDetails(OrderDetailsEntity.builder()
                .id(UUID.randomUUID())
                .service(exampleOldServiceRemoved)
                .build());

        var exampleNewService = ServiceEntity.builder()
                .id(newServiceId)
                .build();
        var exampleRecords = List.of(exampleOldService, exampleNewService);
        Mockito.when(serviceRepository.findAllById(Mockito.any()))
                .thenReturn(exampleRecords);

        // when
        var result = orderHelper.assignServices(exampleOrder, services);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getOrderDetails()).isNotEmpty();
        result.getOrderDetails().forEach(orderDetail -> {
            Assertions.assertThat(orderDetail).isNotNull();
            Assertions.assertThat(exampleRecords).contains(orderDetail.getService());
        });
    }

    @Test
    @DisplayName("Assign technician to order success")
    void testAssignTechnicianToOrderSuccess() {
        // given
        var technicianId = UUID.randomUUID();
        var exampleOrder = new OrderEntity();

        var exampleTechnician = UserEntity.builder()
                .id(technicianId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleTechnician));

        var exampleOrderEntityReturned = OrderEntity.builder()
                .employee(exampleTechnician)
                .status(OrderStatus.IN_SERVICE)
                .build();
        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntityReturned);

        // when
        var result = orderHelper.assignTechnician(exampleOrder, technicianId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getEmployee()).isEqualTo(exampleTechnician);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_SERVICE);
    }

    @Test
    @DisplayName("Same old technician then return order")
    void testSameOldTechnicianReturnOrder() {
        // given
        var technicianId = UUID.randomUUID();
        var exampleOrder = new OrderEntity();

        var exampleTechnician = UserEntity.builder()
                .id(technicianId)
                .build();
        exampleOrder.setEmployee(exampleTechnician);
        Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleTechnician));

        // when
        var result = orderHelper.assignTechnician(exampleOrder, technicianId);

        // then
        Assertions.assertThat(result)
                .isNotNull()
                .isEqualTo(exampleOrder);
    }

    @Test
    @DisplayName("Replace old technician of order success")
    void testReplaceOldTechnicianOfOrderSuccess() {
        // given
        var oldTechnicianId = UUID.randomUUID();
        var newTechnicianId = UUID.randomUUID();
        var exampleOrder = OrderEntity.builder()
                .employee(UserEntity.builder()
                        .id(oldTechnicianId)
                        .build())
                .build();

        var exampleNewTechnician = UserEntity.builder()
                .id(newTechnicianId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(newTechnicianId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleNewTechnician));

        var exampleOrderEntityReturned = OrderEntity.builder()
                .employee(exampleNewTechnician)
                .status(OrderStatus.IN_SERVICE)
                .build();
        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntityReturned);

        // when
        var result = orderHelper.assignTechnician(exampleOrder, newTechnicianId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getEmployee()).isNotNull();
        Assertions.assertThat(result.getEmployee().getId()).isNotEqualTo(oldTechnicianId);
        Assertions.assertThat(result.getEmployee().getId()).isEqualTo(newTechnicianId);
    }

    @Test
    @DisplayName("Already checked out order throws exception")
    void testAlreadyCheckedOutOrder() {
        try {
            // given
            var technicianId = UUID.randomUUID();
            var exampleOrder = OrderEntity.builder()
                    .status(OrderStatus.CHECK_OUT)
                    .build();

            var exampleTechnician = UserEntity.builder()
                    .id(technicianId)
                    .build();
            Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                    .thenReturn(Optional.of(exampleTechnician));

            // when
            orderHelper.assignTechnician(exampleOrder, technicianId);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderAlreadyCheckedOutException.class);
        }
    }

    @Test
    @DisplayName("Assign unknown technician to order throws exception")
    void testAssignTechnicianNotFound() {
        try {
            // given
            var technicianId = UUID.randomUUID();
            var exampleOrder = new OrderEntity();

            Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            // when
            orderHelper.assignTechnician(exampleOrder, technicianId);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(TechnicianNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Assign technician to order by order ID success")
    void testAssignTechnicianToOrderByOrderIdSuccess() {
        // given
        var technicianId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var exampleOrder = OrderEntity.builder()
                .id(orderId)
                .build();

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(exampleOrder));

        var exampleTechnician = UserEntity.builder()
                .id(technicianId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleTechnician));

        var exampleOrderEntityReturned = OrderEntity.builder()
                .employee(exampleTechnician)
                .status(OrderStatus.IN_SERVICE)
                .build();
        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(exampleOrderEntityReturned);

        // when
        var result = orderHelper.assignTechnician(orderId, technicianId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getEmployee()).isEqualTo(exampleTechnician);
        Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_SERVICE);
    }

    @Test
    @DisplayName("Assign technician to order not found throws exception")
    void testAssignTechnicianButOrderNotFound() {
        try {
            // given
            var technicianId = UUID.randomUUID();
            var orderId = UUID.randomUUID();

            Mockito.when(orderRepository.findById(orderId))
                    .thenReturn(Optional.empty());

            // when
            orderHelper.assignTechnician(orderId, technicianId);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderNotFoundException.class);
        }
    }
}
