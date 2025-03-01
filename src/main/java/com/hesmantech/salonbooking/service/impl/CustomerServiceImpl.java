package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckInRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerCheckOutRequest;
import com.hesmantech.salonbooking.api.dto.customer.CustomerRegistrationRequest;
import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.domain.GroupEntity;
import com.hesmantech.salonbooking.domain.OrderDetailsEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.QUserEntity;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.order.OrderAlreadyCheckedOutException;
import com.hesmantech.salonbooking.exception.order.OrderIsWaitingServiceException;
import com.hesmantech.salonbooking.exception.order.OrderNotFoundException;
import com.hesmantech.salonbooking.exception.role.RoleNotFoundException;
import com.hesmantech.salonbooking.helper.order.OrderHelper;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.mapper.base.PhoneNumberMapper;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.repository.OrderedDetailsRepository;
import com.hesmantech.salonbooking.repository.RoleRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hesmantech.salonbooking.constants.Constants.NA;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderedDetailsRepository orderedDetailsRepository;
    private final OrderHelper orderHelper;

    @Override
    public UserEntity create(CustomerRegistrationRequest customerRegistrationRequest) {
        RoleEntity roleEntity = roleRepository.findById(UserRole.ROLE_CUSTOMER.id())
                .orElseThrow(() -> new RoleNotFoundException(UserRole.ROLE_CUSTOMER.id()));

        return userRepository.save(UserEntity.builder()
                .firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .phoneNumber(PhoneNumberMapper.asStandardized(customerRegistrationRequest.phoneNumber()))
                .dob(InstantMapper.from(customerRegistrationRequest.dob()))
                .email(customerRegistrationRequest.email())
                .gender(customerRegistrationRequest.gender())
                .role(roleEntity)
                .status(UserStatus.ACTIVE)
                .build());
    }

    @Transactional
    @Override
    public OrderEntity checkIn(UUID id, CustomerCheckInRequest customerCheckInRequest) {
        return orderHelper.findCustomerAndCreateOrder(id,
                customerCheckInRequest.clientNotes(),
                customerCheckInRequest.technicianId(),
                customerCheckInRequest.services());
    }

    @Override
    public OrderEntity checkOut(UUID id, CustomerCheckOutRequest customerCheckOutRequest) {
        final UUID orderId = customerCheckOutRequest.orderId();

        return orderRepository.findByIdAndCustomer_Id(orderId, id)
                .map(order -> {
                    final OrderStatus orderStatus = order.getStatus();

                    if (OrderStatus.WAITING_SERVICE.equals(orderStatus)) {
                        throw new OrderIsWaitingServiceException(orderId);
                    }

                    if (OrderStatus.CHECK_OUT.equals(orderStatus)) {
                        throw new OrderAlreadyCheckedOutException(orderId);
                    }

                    order = orderHelper.assignTechnician(order, customerCheckOutRequest.technicianId());
                    order.setPrice(customerCheckOutRequest.totalPrice());
                    order.setCheckOutNotes(customerCheckOutRequest.checkOutNotes());
                    order.setDiscount(customerCheckOutRequest.discount());
                    order.setCheckOutTime(Instant.now());
                    order = orderHelper.assignServices(order, customerCheckOutRequest.services());
                    cloneOrderDetailsList(order);
                    order.setStatus(OrderStatus.CHECK_OUT);
                    order.setSubtotal(customerCheckOutRequest.subtotalPrice());

                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(orderId, id));
    }

    @Override
    public List<UserEntity> prepareReportData(Void request) {
        var userEntity = QUserEntity.userEntity;
        var predicate = userEntity.status.ne(UserStatus.DELETED)
                .and(userEntity.role.id.eq(UserRole.ROLE_CUSTOMER.id()));
        var sort = Sort.by(Sort.Direction.DESC, UserSortProperty.FIRST_NAME.getProperty());
        List<UserEntity> customers = new LinkedList<>();

        userRepository.findAll(predicate, sort)
                .forEach(customers::add);

        return customers;
    }

    private void cloneOrderDetailsList(OrderEntity order) {
        final UserEntity employee = order.getEmployee();
        for (OrderDetailsEntity orderDetails : order.getOrderDetails()) {
            OrderedDetailsEntity orderedDetails = toOrderedDetails(orderDetails, employee);
            order.addOrderedDetails(orderedDetails);
            orderedDetailsRepository.save(orderedDetails);
        }
    }

    private OrderedDetailsEntity toOrderedDetails(OrderDetailsEntity orderDetails, UserEntity employee) {
        final ServiceEntity service = orderDetails.getService();
        final GroupEntity group = service.getGroup();

        return OrderedDetailsEntity.builder()
                .employeeId(employee.getId())
                .employeeFirstName(employee.getFirstName())
                .employeeLastName(employee.getLastName())
                .employeePhoneNumber(employee.getPhoneNumber())
                .serviceId(service.getId())
                .serviceName(service.getName())
                .serviceStartPrice(service.getStartPrice())
                .serviceEndPrice(service.getEndPrice())
                .servicePriceType(service.getServicePriceType())
                .serviceGroupName(Optional.ofNullable(group)
                        .map(GroupEntity::getName).orElse(NA))
                .build();
    }
}
