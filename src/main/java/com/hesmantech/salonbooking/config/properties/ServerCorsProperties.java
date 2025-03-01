package com.hesmantech.salonbooking.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

@Component
@ConfigurationProperties(prefix = "server")
@Getter
public class ServerCorsProperties {
    private final CorsConfiguration cors = new CorsConfiguration();
}
