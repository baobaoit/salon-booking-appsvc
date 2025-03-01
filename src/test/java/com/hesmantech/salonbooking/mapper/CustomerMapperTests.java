package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.user.UserGender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CustomerMapperTests {
    private static final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

    @Test
    void testMapToCustomerResponse() {
        // given
        var customer = new UserEntity();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Firstname");
        customer.setLastName("Lastname");
        customer.setEmail("email@email.com");
        customer.setUserId("user-id");
        customer.setGender(UserGender.MALE);

        // when
        var result = customerMapper.toCustomerResponse(customer);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.firstName()).isEqualTo(customer.getFirstName());
        Assertions.assertThat(result.lastName()).isEqualTo(customer.getLastName());
        Assertions.assertThat(result.email()).isEqualTo(customer.getEmail());
        Assertions.assertThat(result.userId()).isEqualTo(customer.getUserId());
        Assertions.assertThat(result.id()).isEqualTo(customer.getId().toString());
        Assertions.assertThat(result.gender()).isEqualTo(customer.getGender().name());
    }

    @Test
    void testMapToCustomerWhenUserNotHasIdAndGenderResponse() {
        // given
        var customer = new UserEntity();
        customer.setFirstName("Firstname");
        customer.setLastName("Lastname");
        customer.setEmail("email@email.com");
        customer.setUserId("user-id");

        // when
        var result = customerMapper.toCustomerResponse(customer);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.firstName()).isEqualTo(customer.getFirstName());
        Assertions.assertThat(result.lastName()).isEqualTo(customer.getLastName());
        Assertions.assertThat(result.email()).isEqualTo(customer.getEmail());
        Assertions.assertThat(result.userId()).isEqualTo(customer.getUserId());
        Assertions.assertThat(result.id()).isNull();
        Assertions.assertThat(result.gender()).isNull();
    }

    @Test
    void testCustomerNull() {
        // when
        var result = customerMapper.toCustomerResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }
}
