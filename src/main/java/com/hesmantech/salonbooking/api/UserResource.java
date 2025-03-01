package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.api.dto.user.SearchUserRequest;
import com.hesmantech.salonbooking.api.dto.user.UpdateUserDetailsRequest;
import com.hesmantech.salonbooking.api.dto.user.UserRegistrationRequest;
import com.hesmantech.salonbooking.api.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "User Resource")
public interface UserResource {
    PageResponse<UserResponse> getAll(@RequestParam(required = false, defaultValue = "0") @Min(value = 0) int page,
                                      @RequestParam(required = false, defaultValue = "10") @Min(value = 1) int size,
                                      @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                      @RequestParam(required = false, defaultValue = "CREATED_DATE") UserSortProperty property,
                                      Principal principal);

    UserResponse getUserDetails(UUID id, Principal principal);

    UserResponse getMyDetails(Principal principal);

    PageResponse<UserResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(value = 0) int page,
                                      @RequestParam(required = false, defaultValue = "10") @Min(value = 1) int size,
                                      @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                      @RequestParam(required = false, defaultValue = "CREATED_DATE") UserSortProperty property,
                                      @RequestBody SearchUserRequest searchUserRequest,
                                      Principal principal);

    UserResponse delete(UUID id, Principal principal);

    UserResponse update(UUID id, @RequestBody UpdateUserDetailsRequest updateUserDetailsRequest, Principal principal);

    UserResponse create(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest, Principal principal);

    ResponseEntity<byte[]> report(@RequestBody SearchUserRequest request, Principal principal);
}
