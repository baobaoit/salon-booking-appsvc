package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class CreditMapperTests {
    private static final CreditMapper creditMapper = CreditMapper.INSTANCE;

    @Test
    void testMapToCustomerCreditDetails() {
        // given
        var creditEntity = new CreditEntity();
        creditEntity.setId(UUID.randomUUID());
        creditEntity.setCreatedDate(Instant.now());
        creditEntity.setLastModifiedDate(Instant.now());

        var customer = new UserEntity();
        customer.setFirstName("Customer First Name");
        customer.setLastName("Customer Last Name");
        creditEntity.setCustomer(customer);

        // when
        var result = creditMapper.toCustomerCreditDetails(creditEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.customerId()).isEqualTo(creditEntity.getId().toString());
        Assertions.assertThat(result.createdDate()).isEqualTo(creditEntity.getCreatedDate().toString());
        Assertions.assertThat(result.lastModifiedDate()).isEqualTo(creditEntity.getLastModifiedDate().toString());
        Assertions.assertThat(result.customerName()).isEqualTo(customer.getFirstName() + " " + customer.getLastName());
    }

    @Test
    void testCreditNull() {
        // when
        var result = creditMapper.toCustomerCreditDetails(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testMapToCustomerCreditDetailsSomeFieldsNull() {
        // given
        var creditEntity = new CreditEntity();

        var customer = new UserEntity();
        customer.setFirstName("Customer First Name");
        customer.setLastName("Customer Last Name");
        creditEntity.setCustomer(customer);

        // when
        var result = creditMapper.toCustomerCreditDetails(creditEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.customerId()).isNull();
        Assertions.assertThat(result.createdDate()).isNull();
        Assertions.assertThat(result.lastModifiedDate()).isNull();
        Assertions.assertThat(result.customerName()).isEqualTo(customer.getFirstName() + " " + customer.getLastName());
    }

    @Test
    void testToSearchCustomerCreditResponse() {
        // given
        var creditEntity = new CreditEntity();
        creditEntity.setId(UUID.randomUUID());

        var customer = new UserEntity();
        customer.setFirstName("Customer First Name");
        customer.setLastName("Customer Last Name");
        creditEntity.setCustomer(customer);

        // when
        var result = creditMapper.toSearchCustomerCreditResponse(creditEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.customerId()).isEqualTo(creditEntity.getId().toString());
        Assertions.assertThat(result.customerName()).isEqualTo(customer.getFirstName() + " " + customer.getLastName());
    }

    @Test
    void testToSearchCustomerCreditResponseWhenCreditEntityIsNull() {
        // when
        var result = creditMapper.toSearchCustomerCreditResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToSearchCustomerCreditResponseWhenIdIsNull() {
        // given
        var creditEntity = new CreditEntity();

        var customer = new UserEntity();
        customer.setFirstName("Customer First Name");
        customer.setLastName("Customer Last Name");
        creditEntity.setCustomer(customer);

        // when
        var result = creditMapper.toSearchCustomerCreditResponse(creditEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.customerId()).isNull();
        Assertions.assertThat(result.customerName()).isEqualTo(customer.getFirstName() + " " + customer.getLastName());
    }
}
