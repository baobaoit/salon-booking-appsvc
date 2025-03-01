package com.hesmantech.salonbooking.security.utils;

import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public final class SecurityUtils {
    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class for Spring Security");
    }

    public static Collection<GrantedAuthority> buildGrantedAuthorities(UserEntity user) {
        if (user == null || user.getRole() == null) {
            return Collections.emptyList();
        }

        return getUserAuthorities(user.getRole())
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    private static Stream<String> getUserAuthorities(RoleEntity role) {
        return Stream.of(role.getId());
    }
}
