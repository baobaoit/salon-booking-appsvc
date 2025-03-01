package com.hesmantech.salonbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.hesmantech.salonbooking.constants.Constants.DEFAULT_SYSTEM_USER;

@Configuration
@EnableJpaAuditing
public class SpringAuditConfig {
    private static final Optional<String> SYSTEM = Optional.of(DEFAULT_SYSTEM_USER);

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .or(() -> SYSTEM);
    }
}
