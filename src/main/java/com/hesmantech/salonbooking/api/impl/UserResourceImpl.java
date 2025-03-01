package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.UserResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.api.dto.user.SearchUserRequest;
import com.hesmantech.salonbooking.api.dto.user.UpdateUserDetailsRequest;
import com.hesmantech.salonbooking.api.dto.user.UserRegistrationRequest;
import com.hesmantech.salonbooking.api.dto.user.UserResponse;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.exception.user.InvalidPhoneNumber;
import com.hesmantech.salonbooking.mapper.UserMapper;
import com.hesmantech.salonbooking.service.ReportService;
import com.hesmantech.salonbooking.service.UserService;
import com.hesmantech.salonbooking.utils.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.hesmantech.salonbooking.constants.Constants.US_PHONE_NUMBER_FORMAT;
import static com.hesmantech.salonbooking.constants.ReportConstants.EMPLOYEE_REPORT_FILE_NAME;
import static com.hesmantech.salonbooking.constants.ReportConstants.TECHNICIAN_REPORT_FILE_NAME;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserResourceImpl implements UserResource {
    private static final Pattern US_PHONE_NUMBER_PATTERN = Pattern.compile(US_PHONE_NUMBER_FORMAT);
    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserService userService;
    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<UserResponse> getAll(int page, int size,
                                             Sort.Direction direction, UserSortProperty property,
                                             Principal principal) {

        try {
            Page<UserResponse> pageUserResponse = userService.getAll(page, size, direction, property)
                    .map(userMapper::toUserResponse);

            log.info("Get all users successfully from {}", principal.getName());

            return PageResponse.<UserResponse>builder()
                    .content(pageUserResponse.getContent())
                    .page(pageUserResponse.getPageable().getPageNumber())
                    .size(pageUserResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageUserResponse.getTotalPages())
                    .totalElements(pageUserResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to get all users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public UserResponse getUserDetails(@PathVariable UUID id, Principal principal) {
        try {
            UserResponse userResponse = userMapper.toUserResponse(userService.getUserDetails(id));

            log.info("Get user details successfully from {}", principal.getName());

            return userResponse;
        } catch (Exception e) {
            log.error("Failed to get user details: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/me")
    @Override
    public UserResponse getMyDetails(Principal principal) {
        try {
            final String username = principal.getName();

            UserResponse userResponse = userMapper.toUserResponse(userService.getMyDetails(username));

            log.info("Get my details successfully from {}", username);

            return userResponse;
        } catch (Exception e) {
            log.error("Failed to get my details: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public PageResponse<UserResponse> search(int page, int size, Sort.Direction direction, UserSortProperty property, SearchUserRequest searchUserRequest, Principal principal) {
        try {
            Page<UserResponse> pageUserResponse = userService.search(page, size, direction, property, searchUserRequest)
                    .map(userMapper::toUserResponse);

            log.info("Search users successfully from {}", principal.getName());

            return PageResponse.<UserResponse>builder()
                    .content(pageUserResponse.getContent())
                    .page(pageUserResponse.getPageable().getPageNumber())
                    .size(pageUserResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageUserResponse.getTotalPages())
                    .totalElements(pageUserResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public UserResponse delete(@PathVariable UUID id, Principal principal) {
        try {
            UserResponse userResponse = userMapper.toUserResponse(userService.delete(id));

            log.info("Delete user {} successfully from {}", id, principal.getName());

            return userResponse;
        } catch (Exception e) {
            log.error("Failed to delete user {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or @securityValidate.canUpdateUser(#id, principal.name)")
    @Override
    public UserResponse update(@PathVariable UUID id, UpdateUserDetailsRequest updateUserDetailsRequest, Principal principal) {
        try {
            if (!PhoneNumberUtils.isValid(updateUserDetailsRequest.phoneNumber())) {
                throw new InvalidPhoneNumber();
            }

            UserResponse updateUser = userMapper.toUserResponse(userService.update(id, updateUserDetailsRequest));

            log.info("Update user {} successfully from {}", id, principal.getName());

            return updateUser;
        } catch (Exception e) {
            log.error("Failed to update user {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public UserResponse create(UserRegistrationRequest userRegistrationRequest, Principal principal) {
        try {
            final String phoneNumber = userRegistrationRequest.phoneNumber();
            if (phoneNumber != null && !US_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
                throw new InvalidPhoneNumber();
            }

            UserResponse createdUser = userMapper.toUserResponse(userService.create(userRegistrationRequest));

            log.info("Create a new user successfully from {}", principal.getName());

            return createdUser;
        } catch (Exception e) {
            log.error("Failed to create a new user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/report")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public ResponseEntity<byte[]> report(SearchUserRequest request, Principal principal) {
        try {
            var data = userService.prepareReportData(request);
            var fileName = request.userRoles()
                    .stream()
                    .findFirst()
                    .filter(UserRole.ROLE_TECHNICIAN::equals)
                    .map(role -> TECHNICIAN_REPORT_FILE_NAME)
                    .orElse(EMPLOYEE_REPORT_FILE_NAME);
            var report = reportService.generateUserReport(fileName, data);

            log.info("Report user successfully from {}", principal.getName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.fileName())
                    .body(report.content());
        } catch (Exception e) {
            log.error("Failed to report user: {}", e.getMessage(), e);
            throw e;
        }
    }
}
