package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.sort.user.UserSortProperty;
import com.hesmantech.salonbooking.api.dto.user.SearchUserRequest;
import com.hesmantech.salonbooking.api.dto.user.UpdateUserDetailsRequest;
import com.hesmantech.salonbooking.api.dto.user.UserRegistrationRequest;
import com.hesmantech.salonbooking.domain.RoleEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.user.UserRole;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.role.RoleNotFoundException;
import com.hesmantech.salonbooking.exception.user.AccountDeletedException;
import com.hesmantech.salonbooking.exception.user.AccountNotFoundException;
import com.hesmantech.salonbooking.repository.RoleRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.service.impl.UserServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        this.userService = new UserServiceImpl(
                userRepository,
                roleRepository
        );
    }

    @Test
    void testGetAllSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = UserSortProperty.CREATED_DATE;

        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));

        // when
        var result = userService.getAll(page, size, direction, property);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isEmpty();
        Assertions.assertThat(result.getPageable()).isNotNull();
        var pageable = result.getPageable();
        Assertions.assertThat(pageable.getPageNumber()).isEqualTo(page);
        Assertions.assertThat(pageable.getPageSize()).isEqualTo(size);
    }

    @Test
    void testGetUserDetailsFailed() {
        try {
            // given
            var userId = UUID.randomUUID();

            Mockito.when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            // when
            userService.getUserDetails(userId);
        } catch (AccountNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(AccountNotFoundException.class);
        }
    }

    @Test
    void testGetUserDetailsSuccess() {
        // given
        var userId = UUID.randomUUID();

        var exampleUserEntity = new UserEntity();
        exampleUserEntity.setId(userId);
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(exampleUserEntity));

        // when
        var result = userService.getUserDetails(userId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void testGetMyDetailsFailed() {
        try {
            // given
            Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            // when
            userService.getMyDetails("username");
        } catch (UsernameNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(UsernameNotFoundException.class);
        }
    }

    @Test
    void testGetMyDetailsSuccess() {
        // given
        var username = "username";

        var exampleUserEntity = new UserEntity();
        exampleUserEntity.setUserId(username);
        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(exampleUserEntity));

        // when
        var result = userService.getMyDetails(username);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUserId()).isEqualTo(username);
    }

    @Test
    void testDeleteFailed() {
        try {
            // given
            Mockito.when(userRepository.findByIdAndStatusIsNotDELETED(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            userService.delete(UUID.randomUUID());
        } catch (AccountDeletedException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(AccountDeletedException.class);
        }
    }

    @Test
    void testDeleteSuccess() {
        // given
        var userId = UUID.randomUUID();

        var exampleUserEntity = UserEntity.builder()
                .id(userId)
                .status(UserStatus.ACTIVE)
                .build();
        Mockito.when(userRepository.findByIdAndStatusIsNotDELETED(userId))
                .thenReturn(Optional.of(exampleUserEntity));

        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(exampleUserEntity);

        // when
        var result = userService.delete(userId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(userId);
        Assertions.assertThat(result.getStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    void testUpdateFailed() {
        try {
            Mockito.when(userRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            userService.update(UUID.randomUUID(), null);
        } catch (AccountNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(AccountNotFoundException.class);
        }
    }

    @Test
    void testUpdateSuccess() {
        // given
        var userId = UUID.randomUUID();
        var request = new UpdateUserDetailsRequest(
                "New first name",
                "New last name",
                null,
                null,
                null,
                null,
                null
        );

        var exampleUserEntity = UserEntity.builder()
                .id(userId)
                .firstName("Old first name")
                .lastName("Old last name")
                .build();
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(exampleUserEntity));

        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(exampleUserEntity);

        // when
        var result = userService.update(userId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(userId);
        Assertions.assertThat(result.getFirstName()).isEqualTo(request.firstName());
        Assertions.assertThat(result.getLastName()).isEqualTo(request.lastName());
    }

    @Test
    void testSearchSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = UserSortProperty.CREATED_DATE;
        var request = new SearchUserRequest(
                null,
                null,
                null,
                null
        );

        Mockito.when(userRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));

        // when
        var result = userService.search(page, size, direction, property, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isEmpty();
        Assertions.assertThat(result.getPageable()).isNotNull();
        var pageable = result.getPageable();
        Assertions.assertThat(pageable.getPageNumber()).isEqualTo(page);
        Assertions.assertThat(pageable.getPageSize()).isEqualTo(size);
    }

    @Test
    void testCreateCustomerFailed() {
        try {
            // given
            var request = new UserRegistrationRequest(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    UserRole.ROLE_CUSTOMER
            );

            // when
            userService.create(request);
        } catch (IllegalStateException e) {
            Assertions.assertThat(e).isInstanceOf(IllegalStateException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Please use the path /api/v1/customers for create a new customer");
        }
    }

    @Test
    void testCreateFailed() {
        try {
            // given
            var request = new UserRegistrationRequest(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    UserRole.ROLE_TECHNICIAN
            );

            Mockito.when(roleRepository.findById(Mockito.anyString()))
                    .thenReturn(Optional.empty());

            // when
            userService.create(request);
        } catch (RoleNotFoundException e) {
            Assertions.assertThat(e).isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Test
    void testCreateSuccess() {
        // given
        var request = new UserRegistrationRequest(
                "First name",
                "Last name",
                null,
                null,
                null,
                null,
                UserRole.ROLE_TECHNICIAN
        );

        var exampleRoleEntity = RoleEntity.builder()
                .id(UserRole.ROLE_TECHNICIAN.id())
                .build();
        Mockito.when(roleRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(exampleRoleEntity));

        var exampleTechnician = UserEntity.builder()
                .role(exampleRoleEntity)
                .firstName("First name")
                .lastName("Last name")
                .build();
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(exampleTechnician);

        // when
        var result = userService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFirstName()).isEqualTo(request.firstName());
        Assertions.assertThat(result.getLastName()).isEqualTo(request.lastName());
        Assertions.assertThat(result.getRole()).isNotNull();
        var role = result.getRole();
        Assertions.assertThat(role.getId()).isEqualTo(exampleRoleEntity.getId());
    }

    @Test
    void testPrepareReportData() {
        // given
        var request = new SearchUserRequest(List.of(UserRole.ROLE_TECHNICIAN), null, null, null);
        var requestEmpty = new SearchUserRequest(List.of(), null, null, null);

        Mockito.when(userRepository.count(Mockito.any(Predicate.class)))
                .thenReturn(1L);

        Mockito.when(userRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 1), 0));

        // when
        var result = userService.prepareReportData(request);
        var resultEmpty = userService.prepareReportData(requestEmpty);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(resultEmpty).isNotNull();
    }

    @Test
    void testPrepareReportDataFailed() {
        try {
            // given
            var request = new SearchUserRequest(List.of(UserRole.ROLE_CUSTOMER), null, null, null);

            // when
            userService.prepareReportData(request);
        } catch (IllegalStateException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(IllegalStateException.class);
        }
    }
}
