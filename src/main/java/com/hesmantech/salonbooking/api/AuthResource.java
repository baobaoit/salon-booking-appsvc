package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.auth.AuthenticationRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Authentication Resource")
public interface AuthResource {
    @SecurityRequirements
    ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthenticationRequest authRequest);
}
