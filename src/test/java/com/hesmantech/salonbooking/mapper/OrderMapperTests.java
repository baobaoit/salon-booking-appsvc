package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.mapper.impl.OrderMapperImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderMapperTests {
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        this.orderMapper = new OrderMapperImpl();
    }

    @Test
    void testToOrderResponse() {
        // given
        var orderEntity = new OrderEntity();
        var customer = new UserEntity();
        customer.setFirstName("Customer First Name");
        customer.setLastName("Customer Last Name");
        orderEntity.setCustomer(customer);

        // when
        var result = orderMapper.toOrderResponse(orderEntity);

        // then
        Assertions.assertThat(result).isNotNull();
    }
}
