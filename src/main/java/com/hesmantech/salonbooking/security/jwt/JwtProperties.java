package com.hesmantech.salonbooking.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    // validity in milliseconds
    private long validityInMs; // 1h
    private String secretKey;
}
