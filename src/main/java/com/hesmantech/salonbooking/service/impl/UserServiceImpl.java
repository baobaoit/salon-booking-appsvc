package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.api.dto.user.SearchUserRequest;
import com.hesmantech.salonbooking.api.dto.user.UpdateUserDetailsRequest;
import com.hesmantech.salonbooking.api.dto.user.UserRegistrationRequest;
import com.hesmantech.salonbooking.domain.QUserEntity;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.role.RoleNotFoundException;
import com.hesmantech.salonbooking.exception.user.AccountDeletedException;
import com.hesmantech.salonbooking.exception.user.AccountNotFoundException;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.mapper.base.PhoneNumberMapper;
import com.hesmantech.salonbooking.repository.RoleRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.UserSearchParamsBuilder;
import com.hesmantech.salonbooking.service.UserService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<UserEntity> getAll(int page, int size, Sort.Direction direction, UserSortProperty property) {
        final Pageable pageable = PageRequest.of(page, size, direction, property.getProperty());

        return userRepository.findAll(pageable);
    }

    @Override
    public UserEntity getUserDetails(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    public UserEntity getMyDetails(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public Page<UserEntity> search(int page, int size, Sort.Direction direction,
                                   UserSortProperty property, SearchUserRequest searchUserRequest) {

        final UserSearchParamsBuilder searchParamsBuilder = UserSearchParamsBuilder
                .from(page, size, direction, property, searchUserRequest);
        final BooleanExpression criteria = searchParamsBuilder.getCommonCriteriaValue();
        final Pageable pageable = searchParamsBuilder.getPageable();

        log.info("Search User with criteria: {}", criteria);

        return userRepository.findAll(criteria, pageable);
    }

    @Override
    public UserEntity delete(UUID id) {
        return userRepository.findByIdAndStatusIsNotDELETED(id)
                .map(userEntity -> {
                    userEntity.setStatus(UserStatus.DELETED);

                    return userRepository.save(userEntity);
                })
                .orElseThrow(() -> new AccountDeletedException(id));
    }

    @Override
    public UserEntity update(UUID id, UpdateUserDetailsRequest updateUserDetailsRequest) {
        return userRepository.findById(id)
                .map(userEntity -> {
                    userEntity.setFirstName(updateUserDetailsRequest.firstName());
                    userEntity.setLastName(updateUserDetailsRequest.lastName());
                    userEntity.setUserId(updateUserDetailsRequest.userId());
                    userEntity.setPhoneNumber(PhoneNumberMapper.asStandardized(updateUserDetailsRequest.phoneNumber()));
                    userEntity.setDob(InstantMapper.from(updateUserDetailsRequest.dob()));
                    userEntity.setEmail(updateUserDetailsRequest.email());
                    userEntity.setGender(updateUserDetailsRequest.gender());

                    return userRepository.save(userEntity);
                })
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    public UserEntity create(UserRegistrationRequest userRegistrationRequest) {
        UserRole role = userRegistrationRequest.role();
        if (UserRole.ROLE_CUSTOMER.equals(role)) {
            throw new IllegalStateException("Please use the path /api/v1/customers for create a new customer");
        }

        final String roleId = role.id();
        RoleEntity roleEntity = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        return userRepository.save(UserEntity.builder()
                .firstName(userRegistrationRequest.firstName())
                .lastName(userRegistrationRequest.lastName())
                .phoneNumber(PhoneNumberMapper.asStandardized(userRegistrationRequest.phoneNumber()))
                .dob(InstantMapper.from(userRegistrationRequest.dob()))
                .email(userRegistrationRequest.email())
                .gender(userRegistrationRequest.gender())
                .role(roleEntity)
                .status(UserStatus.ACTIVE)
                .build());
    }

    @Override
    public List<UserEntity> prepareReportData(SearchUserRequest request) {
        var countPredicate = buildCountPredicate(request.userRoles());
        var size = (int) userRepository.count(countPredicate);
        var page = search(0, size, Sort.Direction.ASC, UserSortProperty.FIRST_NAME, request);

        return page.getContent();
    }

    private BooleanExpression buildCountPredicate(Collection<UserRole> userRoles) {
        if (userRoles.stream().anyMatch(UserRole.ROLE_CUSTOMER::equals)) {
            throw new IllegalStateException("Please use the path /api/v1/customers/report");
        }

        var userEntity = QUserEntity.userEntity;
        var countPredicate = userEntity.status.ne(UserStatus.DELETED);

        return userRoles.isEmpty() ? countPredicate :
                countPredicate.and(userEntity.role.id.in(userRoles.stream()
                        .map(UserRole::id)
                        .toList()));
    }
}
