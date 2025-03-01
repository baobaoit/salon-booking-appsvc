package com.hesmantech.salonbooking.config;

import com.hesmantech.salonbooking.config.properties.ServerCorsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class CorsConfig {
    private final ServerCorsProperties serverCorsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        log.info("Registering CORS configuration");
        source.registerCorsConfiguration("/**", serverCorsProperties.getCors());
        return source;
    }
}
