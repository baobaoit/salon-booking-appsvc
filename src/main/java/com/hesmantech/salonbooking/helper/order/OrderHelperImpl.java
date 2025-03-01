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
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderHelperImpl implements OrderHelper {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    @Override
    public OrderEntity findCustomerAndCreateOrder(UUID customerId, String customerNotes, UUID technicianId, Collection<UUID> serviceIds) {
        UserEntity customer = userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return createOrderAndOrderDetails(customer, customerNotes, technicianId, serviceIds);
    }

    @Override
    public OrderEntity assignServices(OrderEntity order, Collection<UUID> services) {
        List<OrderDetailsEntity> orderDetailsEntities = order.getOrderDetails();

        if (CollectionUtils.isEmpty(orderDetailsEntities)) {
            return findServiceAndCreateOrderDetails(order, services);
        }

        List<OrderDetailsEntity> removeOrderDetailsList = orderDetailsEntities.stream()
                .filter(o -> services.stream().noneMatch(o.getService().getId()::equals))
                .toList();

        for (OrderDetailsEntity orderDetailsEntity : removeOrderDetailsList) {
            order.removeOrderDetails(orderDetailsEntity);
            orderDetailsRepository.delete(orderDetailsEntity);
        }

        List<UUID> oldServiceIds = orderDetailsEntities.stream()
                .map(OrderDetailsEntity::getService)
                .map(ServiceEntity::getId)
                .toList();

        List<UUID> newOrderDetailsList = services.stream()
                .filter(s -> oldServiceIds.stream().noneMatch(s::equals))
                .toList();

        return findServiceAndCreateOrderDetails(order, newOrderDetailsList);
    }

    @Override
    public OrderEntity assignTechnician(UUID id, UUID technicianId) {
        return orderRepository.findById(id)
                .map(order -> assignTechnician(order, technicianId))
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public OrderEntity assignTechnician(OrderEntity order, UUID technicianId) {
        return userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE)
                .map(newTechnician -> {
                    if (OrderStatus.CHECK_OUT.equals(order.getStatus())) {
                        throw new OrderAlreadyCheckedOutException(order.getId());
                    }

                    UserEntity oldTechnician = order.getEmployee();
                    if (oldTechnician != null && oldTechnician.getId().equals(technicianId)) {
                        return order;
                    }

                    order.setEmployee(newTechnician);
                    order.setStatus(OrderStatus.IN_SERVICE);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new TechnicianNotFoundException(technicianId));
    }

    private OrderEntity createOrderAndOrderDetails(UserEntity customer, String customerNotes, UUID technicianId, Collection<UUID> serviceIds) {
        UserEntity technician = null;
        if (technicianId != null) {
            technician = userRepository.findByIdAndStatus(technicianId, UserStatus.ACTIVE)
                    .orElseThrow(() -> new TechnicianNotFoundException(technicianId));
        }

        final OrderStatus orderStatus = technician == null ? OrderStatus.WAITING_SERVICE : OrderStatus.IN_SERVICE;

        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .customer(customer)
                .employee(technician)
                .status(orderStatus)
                .customerNotes(customerNotes)
                .orderDetails(new ArrayList<>())
                .build());

        return findServiceAndCreateOrderDetails(order, serviceIds);
    }

    private OrderEntity findServiceAndCreateOrderDetails(OrderEntity order, Collection<UUID> serviceIds) {
        if (serviceIds.isEmpty()) {
            return order;
        }

        for (ServiceEntity service : serviceRepository.findAllById(serviceIds)) {
            OrderDetailsEntity orderDetails = OrderDetailsEntity.builder()
                    .service(service)
                    .build();

            order.addOrderDetails(orderDetails);
            orderDetailsRepository.save(orderDetails);
        }

        return order;
    }
}
