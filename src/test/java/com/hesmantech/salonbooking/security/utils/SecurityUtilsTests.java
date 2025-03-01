package com.hesmantech.salonbooking.security.utils;

import com.hesmantech.salonbooking.base.AbstractUserTests;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Stream;

class SecurityUtilsTests extends AbstractUserTests {
    private final Logger log = LoggerFactory.getLogger(SecurityUtilsTests.class);

    @Test
    @DisplayName("Check private constructor")
    void testPrivateConstructor() {
        try {
            // given
            var constructor = SecurityUtils.class.getDeclaredConstructor();

            // when
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            // then
            Assertions.assertInstanceOf(UnsupportedOperationException.class, e.getCause());
        }
    }

    @Test
    @DisplayName("Verify the private method called getUserAuthorities.")
    @SuppressWarnings("unchecked")
    void testGetUserAuthorities() {
        try {
            // given
            var getUserAuthoritiesMethod = SecurityUtils.class.getDeclaredMethod("getUserAuthorities", RoleEntity.class);
            var roleEntity = RoleEntity.builder()
                    .id(ROLE_ID_ROLE_TESTER)
                    .build();

            // when
            getUserAuthoritiesMethod.setAccessible(true);
            var streamUserAuthorities = (Stream<String>) getUserAuthoritiesMethod.invoke(null, roleEntity);

            // then
            streamUserAuthorities.forEach(authority -> Assertions.assertEquals(ROLE_ID_ROLE_TESTER, authority));
        } catch (Exception e) {
            log.error("Test get user authorities failed: {}", e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Returns an empty collection when input is invalid")
    void testBuildGrantedAuthoritiesReturnsEmptyCollection() {
        // given
        var userHasRoleIsNull = new UserEntity();
        userHasRoleIsNull.setRole(null);

        // when
        var resultUserIsNull = SecurityUtils.buildGrantedAuthorities(null);
        var resultUserHasRoleIsNull = SecurityUtils.buildGrantedAuthorities(userHasRoleIsNull);

        // then
        Assertions.assertTrue(resultUserIsNull.isEmpty());
        Assertions.assertTrue(resultUserHasRoleIsNull.isEmpty());
    }

    @Test
    @DisplayName("Returns a collection of user authorities")
    void testBuildGrantedAuthoritiesReturnsUserAuthorities() {
        // given

        // when
        var userAuthorities = SecurityUtils.buildGrantedAuthorities(exampleUser);

        // then
        Assertions.assertNotNull(userAuthorities);
        Assertions.assertInstanceOf(List.class, userAuthorities);
        Assertions.assertEquals(1, userAuthorities.size());
        userAuthorities.forEach(authority -> {
            Assertions.assertInstanceOf(GrantedAuthority.class, authority);
            Assertions.assertEquals(ROLE_ID_ROLE_TESTER, authority.getAuthority());
        });
    }
}
