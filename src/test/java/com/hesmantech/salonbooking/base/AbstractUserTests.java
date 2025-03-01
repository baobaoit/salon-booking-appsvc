package com.hesmantech.salonbooking.base;

import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public abstract class AbstractUserTests {
    protected final String ROLE_ID_ROLE_TESTER = "ROLE_TESTER";
    protected final String ADMIN_USERNAME = "ADMIN-USERNAME";
    protected final String ADMIN_FIRST_NAME = "Admin";
    protected final String ADMIN_LAST_NAME = "Username";

    protected UserEntity exampleUser;

    @BeforeEach
    protected void setUp() {
        var passwordEncoder = new BCryptPasswordEncoder();
        this.exampleUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .role(RoleEntity.builder()
                        .id(ROLE_ID_ROLE_TESTER)
                        .build())
                .userId(ADMIN_USERNAME)
                .firstName(ADMIN_FIRST_NAME)
                .lastName(ADMIN_LAST_NAME)
                .password(passwordEncoder.encode("bXlzYWxvbmJvb2tpbmcuc2Fsb24"))
                .build();
    }
}
