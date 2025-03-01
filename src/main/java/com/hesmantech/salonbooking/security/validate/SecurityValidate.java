package com.hesmantech.salonbooking.security.validate;

import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.exception.user.AccountNotFoundException;
import com.hesmantech.salonbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityValidate {
    private final UserRepository userRepository;

    public boolean canUpdateUser(UUID id, String username) {
        return userRepository.findByUsername(username)
                .map(UserEntity::getId)
                .map(id::equals)
                .orElseThrow(() -> new AccountNotFoundException(username));
    }
}
