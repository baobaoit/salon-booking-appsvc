package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import com.hesmantech.salonbooking.domain.model.user.UserGender;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InaccessibleObjectException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class UserMapperTests {
    private static final Logger log = LoggerFactory.getLogger(UserMapperTests.class);
    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void testToUserResponseFailed() {
        // when
        var result = userMapper.toUserResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToUserResponseSuccess() {
        // given
        var userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        var roleEntity = new RoleEntity();
        roleEntity.setName("User");
        userEntity.setRole(roleEntity);
        userEntity.setGender(UserGender.MALE);
        userEntity.setStatus(UserStatus.ACTIVE);

        // when
        var result = userMapper.toUserResponse(userEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(userEntity.getId().toString());
        Assertions.assertThat(result.role()).isEqualTo(roleEntity.getName());
        Assertions.assertThat(result.gender()).isEqualTo(UserGender.MALE.toString());
        Assertions.assertThat(result.status()).isEqualTo(UserStatus.ACTIVE.toString());
    }

    @Test
    void testToUserResponseSomeNullsSuccess() {
        // given
        var userEntity = new UserEntity();
        userEntity.setRole(new RoleEntity());

        // when
        var result = userMapper.toUserResponse(userEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
        Assertions.assertThat(result.role()).isNull();
        Assertions.assertThat(result.gender()).isNull();
        Assertions.assertThat(result.status()).isNull();
    }

    @Test
    void testToUserResponseRoleNullSuccess() {
        // given
        var userEntity = new UserEntity();

        // when
        var result = userMapper.toUserResponse(userEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
        Assertions.assertThat(result.role()).isNull();
        Assertions.assertThat(result.gender()).isNull();
        Assertions.assertThat(result.status()).isNull();
    }

    @Test
    void testUserRoleNameFailed() {
        try {
            // given
            var userRoleName = userMapper.getClass()
                    .getDeclaredMethod("userRoleName", UserEntity.class);
            userRoleName.setAccessible(true);

            // when
            var result = userRoleName.invoke(userMapper, (UserEntity) null);

            // then
            Assertions.assertThat(result).isNull();
        } catch (NoSuchMethodException e) {
            log.error("The private method is not found: {}", e.getMessage(), e);
        } catch (InaccessibleObjectException e) {
            log.error("Access can not be enabled: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception message: {}", e.getMessage(), e);
        }
    }

    @Test
    void testToNailTechnicianFailed() {
        // when
        var result = userMapper.toNailTechnicianResponse((UserEntity) null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToNailTechnicianSuccess() {
        // given
        var userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());

        // when
        var result = userMapper.toNailTechnicianResponse(userEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(userEntity.getId().toString());
    }

    @Test
    void testToNailTechnicianUserIdNullSuccess() {
        // given
        var userEntity = new UserEntity();

        // when
        var result = userMapper.toNailTechnicianResponse(userEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
    }

    @Test
    void testToNailTechnicianFromOrderFailed() {
        // when
        var result = userMapper.toNailTechnicianResponse((OrderedDetailsEntity) null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToNailTechnicianFromOrderSuccess() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CHECK_OUT);
        var orderedDetailsEntity = new OrderedDetailsEntity();
        orderedDetailsEntity.setEmployeeId(UUID.randomUUID());
        orderEntity.setOrderedDetails(List.of(orderedDetailsEntity));

        // when
        var result = userMapper.toNailTechnicianResponse(orderEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(orderedDetailsEntity.getEmployeeId().toString());
    }

    @Test
    void testToNailTechnicianFromOrderInServiceSuccess() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.IN_SERVICE);
        orderEntity.setEmployee(new UserEntity());

        // when
        var result = userMapper.toNailTechnicianResponse(orderEntity);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testToNailTechnicianFromOrderEmployeeIdIsNullSuccess() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CHECK_OUT);
        var orderedDetailsEntity = new OrderedDetailsEntity();
        orderEntity.setOrderedDetails(List.of(orderedDetailsEntity));

        // when
        var result = userMapper.toNailTechnicianResponse(orderEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
    }

    @Test
    void testToCustomerGiftCardResponseFailed() {
        // when
        var result = userMapper.toCustomerGiftCardResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToCustomerGiftCardResponseSuccess() {
        // given
        var customerGiftCardEntity = new CustomerGiftCardEntity();
        customerGiftCardEntity.setCustomer(new UserEntity());

        // when
        var result = userMapper.toCustomerGiftCardResponse(customerGiftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testToCustomerGiftCardResponseSetFailed() {
        // when
        var result = userMapper.toCustomerGiftCardResponseSet(null);

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void testToCustomerGiftCardResponseSetSuccess() {
        // given
        var customerGiftCardEntity = new CustomerGiftCardEntity();
        var customers = Set.of(customerGiftCardEntity);

        // when
        var result = userMapper.toCustomerGiftCardResponseSet(customers);

        // then
        Assertions.assertThat(result).isNotEmpty();
    }
}
