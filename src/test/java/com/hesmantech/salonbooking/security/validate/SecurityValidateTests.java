package com.hesmantech.salonbooking.security.validate;

import com.hesmantech.salonbooking.base.AbstractUserTests;
import com.hesmantech.salonbooking.exception.user.AccountNotFoundException;
import com.hesmantech.salonbooking.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SecurityValidateTests extends AbstractUserTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityValidate securityValidate;

    @Test
    @DisplayName("Valid to update a user")
    void testCanUpdateUserReturnTrue() {
        // given
        Mockito.when(userRepository.findByUsername(ADMIN_USERNAME))
                .thenReturn(Optional.of(exampleUser));

        // when
        var result = securityValidate.canUpdateUser(exampleUser.getId(), ADMIN_USERNAME);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Throws AccountNotFoundException when username not found")
    void testCanUpdateUserThrowsAccountNotFoundException() {
        try {
            // given
            Mockito.when(userRepository.findByUsername(ADMIN_USERNAME))
                    .thenReturn(Optional.empty());

            // when
            securityValidate.canUpdateUser(exampleUser.getId(), ADMIN_USERNAME);
        } catch (Exception e) {
            // then
            Assertions.assertInstanceOf(AccountNotFoundException.class, e);
        }
    }
}
