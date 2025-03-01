package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.api.dto.user.SearchUserRequest;
import com.hesmantech.salonbooking.api.dto.user.UpdateUserDetailsRequest;
import com.hesmantech.salonbooking.api.dto.user.UserRegistrationRequest;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.service.report.PrepareReportData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface UserService extends PrepareReportData<UserEntity, SearchUserRequest> {
    Page<UserEntity> getAll(int page, int size, Sort.Direction direction, UserSortProperty property);

    UserEntity getUserDetails(UUID id);

    UserEntity getMyDetails(String username);

    Page<UserEntity> search(int page, int size, Sort.Direction direction,
                            UserSortProperty property, SearchUserRequest searchUserRequest);

    UserEntity delete(UUID id);

    UserEntity update(UUID id, UpdateUserDetailsRequest updateUserDetailsRequest);

    UserEntity create(UserRegistrationRequest userRegistrationRequest);
}
