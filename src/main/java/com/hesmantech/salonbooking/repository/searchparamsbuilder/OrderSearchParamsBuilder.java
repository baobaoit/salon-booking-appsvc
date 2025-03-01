package com.hesmantech.salonbooking.repository.searchparamsbuilder;

import com.hesmantech.salonbooking.api.dto.order.SearchOrderRequest;
import com.hesmantech.salonbooking.api.dto.sort.order.OrderSortProperty;
import com.hesmantech.salonbooking.domain.QOrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.QUserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.base.AbstractSearchParamsBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.hesmantech.salonbooking.domain.QOrderEntity.orderEntity;

public class OrderSearchParamsBuilder extends AbstractSearchParamsBuilder {
    private final OrderSortProperty property;
    private final UUID customerId;
    private final List<OrderStatus> statuses;
    private final List<UserStatus> customerStatuses;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String technicianName;
    private final String customerName;

    private OrderSearchParamsBuilder(Builder builder) {
        super(builder);
        this.property = builder.property;
        this.customerId = builder.customerId;
        this.statuses = builder.statuses;
        this.customerStatuses = builder.customerStatuses;
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
        this.technicianName = builder.technicianName;
        this.customerName = builder.customerName;
    }

    public static OrderSearchParamsBuilder from(int page, int size, Sort.Direction direction, OrderSortProperty property,
                                                SearchOrderRequest searchOrderRequest) {
        return new Builder()
                .withPage(page)
                .withSize(size)
                .withDirection(direction)
                .withProperty(property)
                .withCustomerId(searchOrderRequest.customerId())
                .withStatuses(searchOrderRequest.statuses())
                .withCustomerStatuses(searchOrderRequest.customerStatuses())
                .withFromDate(searchOrderRequest.fromDate())
                .withToDate(searchOrderRequest.toDate())
                .withTechnicianName(searchOrderRequest.technicianName())
                .withCustomerName(searchOrderRequest.customerName())
                .build();
    }

    @Override
    public Optional<BooleanExpression> getCommonCriteria() {
        final QUserEntity customerEntity = orderEntity.customer;

        return Stream.of(
                        Optional.ofNullable(customerId).map(orderEntity.customer.id::eq),
                        Optional.of(statuses).filter(CollectionUtils::isNotEmpty)
                                .map(orderEntity.status::in),
                        Optional.ofNullable(fromDate).map(InstantMapper::from)
                                .map(orderEntity.createdDate::goe),
                        Optional.ofNullable(toDate).map(d -> d.plusDays(1))
                                .map(InstantMapper::from)
                                .map(orderEntity.createdDate::lt),
                        Optional.of(customerStatuses).map(sttList -> sttList.isEmpty() ?
                                customerEntity.status.ne(UserStatus.DELETED) :
                                customerEntity.status.in(sttList)),
                        Optional.ofNullable(technicianName).map(this::wrapInPercentSymbols)
                                .map(this::mapTechnicianName),
                        Optional.ofNullable(customerName).map(this::wrapInPercentSymbols)
                                .map(customerEntity.firstName
                                        .concat(" ")
                                        .concat(customerEntity.lastName)::likeIgnoreCase))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(BooleanExpression::and);
    }

    private BooleanExpression mapTechnicianName(String technicianName) {
        final QUserEntity employee = orderEntity.employee;
        final QOrderedDetailsEntity orderedDetailsEntity = orderEntity.orderedDetails.any();

        final BooleanExpression findTechnicianNameInOrdered = orderEntity.status.eq(OrderStatus.CHECK_OUT)
                .and(orderedDetailsEntity.employeeFirstName
                        .concat(" ")
                        .concat(orderedDetailsEntity.employeeLastName)
                        .likeIgnoreCase(technicianName));

        final BooleanExpression findTechnicianNameInOrder = orderEntity.status.ne(OrderStatus.CHECK_OUT)
                .and(employee.firstName
                        .concat(" ")
                        .concat(employee.lastName)
                        .likeIgnoreCase(technicianName));

        return findTechnicianNameInOrder.or(findTechnicianNameInOrdered);
    }

    @Override
    public Pageable getPageable() {
        return super.getPageable(property.getProperty());
    }

    public static class Builder extends AbstractSearchParamsBuilder.Builder<Builder> {
        private OrderSortProperty property;
        private UUID customerId;
        private List<OrderStatus> statuses;
        private List<UserStatus> customerStatuses;
        private LocalDate fromDate;
        private LocalDate toDate;
        private String technicianName;
        private String customerName;

        public Builder withProperty(OrderSortProperty property) {
            this.property = property;
            return this;
        }

        public Builder withCustomerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withStatuses(List<OrderStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder withCustomerStatuses(List<UserStatus> customerStatuses) {
            this.customerStatuses = customerStatuses;
            return this;
        }

        public Builder withFromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder withToDate(LocalDate toDate) {
            this.toDate = toDate;
            return this;
        }

        public Builder withTechnicianName(String technicianName) {
            this.technicianName = technicianName;
            return this;
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public OrderSearchParamsBuilder build() {
            return new OrderSearchParamsBuilder(this);
        }
    }
}
