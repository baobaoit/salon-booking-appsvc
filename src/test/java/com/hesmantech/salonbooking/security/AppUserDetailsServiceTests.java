package com.hesmantech.salonbooking.security;

import com.hesmantech.salonbooking.base.AbstractUserTests;
import com.hesmantech.salonbooking.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTests extends AbstractUserTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    @DisplayName("Load user by username successfully")
    void testLoadUserByUsername() {
        // given
        Mockito.when(userRepository.findByUsername(ADMIN_USERNAME))
                .thenReturn(Optional.of(exampleUser));

        // when
        var userDetails = appUserDetailsService.loadUserByUsername(ADMIN_USERNAME);

        // then
        Assertions.assertThat(userDetails).isNotNull();
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(ADMIN_USERNAME);
    }

    @Test
    @DisplayName("Failed to load user by username")
    void testLoadUserByUsernameNotFound() {
        try {
            // given
            Mockito.when(userRepository.findByUsername(ADMIN_USERNAME))
                    .thenReturn(Optional.empty());

            // when
            appUserDetailsService.loadUserByUsername(ADMIN_USERNAME);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
