package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.giftcard.CreateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.UpdateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.CustomerGiftCardId;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardDoesNotBelongToCustomerException;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardIsNotRedeemableException;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardNotFoundException;
import com.hesmantech.salonbooking.exception.user.CustomerNotFoundException;
import com.hesmantech.salonbooking.helper.giftcard.GiftCardHelper;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.service.impl.GiftCardServiceImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.hesmantech.salonbooking.constants.Constants.DEFAULT_SYSTEM_USER;

@ExtendWith(MockitoExtension.class)
class GiftCardServiceTests {
    @Mock
    private GiftCardRepository giftCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerGiftCardRepository customerGiftCardRepository;

    @Mock
    private GiftCardHelper giftCardHelper;

    private GiftCardService giftCardService;

    @BeforeEach
    void setUp() {
        this.giftCardService = new GiftCardServiceImpl(
                giftCardRepository,
                userRepository,
                customerGiftCardRepository,
                giftCardHelper
        );
    }

    @Test
    void testGetDetailsFailed() {
        try {
            // given
            Mockito.when(giftCardRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            giftCardService.getDetails(UUID.randomUUID());
        } catch (GiftCardNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardNotFoundException.class);
        }
    }

    @Test
    void testGetDetailsSuccess() {
        // given
        var giftCardId = UUID.randomUUID();
        var createdByUsername = "0987654321";
        var createdByFirstName = "Created by first name";
        var createdByLastName = "Created by last name";
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .build();
        exampleGiftCardEntity.setCreatedBy(createdByUsername);
        Mockito.when(giftCardRepository.findById(giftCardId))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        var exampleUserEntity = UserEntity.builder()
                .firstName(createdByFirstName)
                .lastName(createdByLastName)
                .phoneNumber(createdByUsername)
                .build();
        Mockito.when(userRepository.findByUsername(createdByUsername))
                .thenReturn(Optional.of(exampleUserEntity));

        // when
        var result = giftCardService.getDetails(giftCardId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(giftCardId);
        Assertions.assertThat(result.getCreatedBy()).isEqualTo(createdByFirstName + " " + createdByLastName);
    }

    @Test
    void testGetDetailsSystemUserSuccess() {
        // given
        var giftCardIdNull = UUID.randomUUID();
        var giftCardIdEmpty = UUID.randomUUID();
        var giftCardIdSystem = UUID.randomUUID();

        var exampleGiftCardEntityNull = GiftCardEntity.builder()
                .id(giftCardIdNull)
                .build();

        var exampleGiftCardEntityEmpty = GiftCardEntity.builder()
                .id(giftCardIdEmpty)
                .build();
        exampleGiftCardEntityEmpty.setCreatedBy("");

        var exampleGiftCardEntitySystem = GiftCardEntity.builder()
                .id(giftCardIdSystem)
                .build();
        exampleGiftCardEntitySystem.setCreatedBy(DEFAULT_SYSTEM_USER);

        Mockito.when(giftCardRepository.findById(Mockito.any(UUID.class)))
                .thenAnswer((Answer<Optional<GiftCardEntity>>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();

                    var giftCardId = (UUID) args[0];

                    if (giftCardIdNull.equals(giftCardId)) {
                        return Optional.of(exampleGiftCardEntityNull);
                    }

                    if (giftCardIdEmpty.equals(giftCardId)) {
                        return Optional.of(exampleGiftCardEntityEmpty);
                    }

                    return Optional.of(exampleGiftCardEntitySystem);
                });

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(Mockito.any(GiftCardEntity.class), Mockito.eq(true)))
                .thenAnswer((Answer<GiftCardEntity>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();

                    var giftCardEntity = (GiftCardEntity) args[0];
                    var giftCardId = giftCardEntity.getId();

                    if (giftCardIdNull.equals(giftCardId)) {
                        return exampleGiftCardEntityNull;
                    }

                    if (giftCardIdEmpty.equals(giftCardId)) {
                        return exampleGiftCardEntityEmpty;
                    }

                    return exampleGiftCardEntitySystem;
                });

        // when
        var resultNull = giftCardService.getDetails(giftCardIdNull);
        var resultEmpty = giftCardService.getDetails(giftCardIdEmpty);
        var resultSystem = giftCardService.getDetails(giftCardIdSystem);

        // then
        Assertions.assertThat(resultNull).isNotNull();
        Assertions.assertThat(resultNull.getId()).isEqualTo(giftCardIdNull);
        Assertions.assertThat(resultNull.getCreatedBy()).isNull();

        Assertions.assertThat(resultEmpty).isNotNull();
        Assertions.assertThat(resultEmpty.getId()).isEqualTo(giftCardIdEmpty);
        Assertions.assertThat(resultEmpty.getCreatedBy()).isEmpty();

        Assertions.assertThat(resultSystem).isNotNull();
        Assertions.assertThat(resultSystem.getId()).isEqualTo(giftCardIdSystem);
        Assertions.assertThat(resultSystem.getCreatedBy()).isEqualTo(DEFAULT_SYSTEM_USER);
    }

    @Test
    void testGenerateGiftCardCodeSuccess() {
        // given
        Mockito.when(giftCardRepository.existsByCode(Mockito.anyString()))
                .thenReturn(true)
                .thenReturn(false);

        // when
        var result = giftCardService.generateGiftCardCode();

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void testDeactiveFailed() {
        try {
            // given
            Mockito.when(giftCardRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            giftCardService.deactive(UUID.randomUUID());
        } catch (GiftCardNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardNotFoundException.class);
        }
    }

    @Test
    void testDeactiveSuccess() {
        // given
        var giftCardId = UUID.randomUUID();
        var createdByUsername = "0987654321";
        var createdByFirstName = "Created by first name";
        var createdByLastName = "Created by last name";
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .status(GiftCardStatus.FULL)
                .build();
        exampleGiftCardEntity.setCreatedBy(createdByUsername);
        Mockito.when(giftCardRepository.findById(giftCardId))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        var exampleUserEntity = UserEntity.builder()
                .firstName(createdByFirstName)
                .lastName(createdByLastName)
                .phoneNumber(createdByUsername)
                .build();
        Mockito.when(userRepository.findByUsername(createdByUsername))
                .thenReturn(Optional.of(exampleUserEntity));

        // when
        var result = giftCardService.deactive(giftCardId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(giftCardId);
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.DEACTIVATED);
    }

    @Test
    void testDeactiveGiftCardHasStatusDeactiveThenReturn() {
        // given
        var giftCardId = UUID.randomUUID();
        var createdByUsername = "0987654321";
        var createdByFirstName = "Created by first name";
        var createdByLastName = "Created by last name";
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .status(GiftCardStatus.DEACTIVATED)
                .build();
        exampleGiftCardEntity.setCreatedBy(createdByUsername);
        Mockito.when(giftCardRepository.findById(giftCardId))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        var exampleUserEntity = UserEntity.builder()
                .firstName(createdByFirstName)
                .lastName(createdByLastName)
                .phoneNumber(createdByUsername)
                .build();
        Mockito.when(userRepository.findByUsername(createdByUsername))
                .thenReturn(Optional.of(exampleUserEntity));

        // when
        var result = giftCardService.deactive(giftCardId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(giftCardId);
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.DEACTIVATED);
    }

    @Test
    void testCreateGiftCardSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var request = new CreateGiftCardRequest(
                "TEST",
                1d,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1_000,
                "New gift card",
                Set.of(customerId)
        );

        var exampleGiftCardEntity = GiftCardEntity.builder()
                .code(request.giftCode())
                .initialValue(request.initialValue())
                .notes(request.notes())
                .customers(new HashSet<>())
                .build();
        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(exampleGiftCardEntity);

        var exampleUserEntity = UserEntity.builder()
                .id(customerId)
                .giftCards(new HashSet<>())
                .build();
        Mockito.when(userRepository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(exampleUserEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        // when
        var result = giftCardService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCode()).isEqualTo(request.giftCode());
        Assertions.assertThat(result.getInitialValue()).isEqualTo(request.initialValue());
        Assertions.assertThat(result.getNotes()).isEqualTo(request.notes());
    }

    @Test
    void testUpdateFailed() {
        try {
            // given
            Mockito.when(giftCardRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            giftCardService.update(UUID.randomUUID(), null);
        } catch (GiftCardNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardNotFoundException.class);
        }
    }

    @Test
    void testUpdateSuccess() {
        // given
        var giftCardId = UUID.randomUUID();
        var newCustomerId = UUID.randomUUID();
        var createdByUsername = "0987654321";
        var createdByFirstName = "Created by first name";
        var createdByLastName = "Created by last name";
        var request = new UpdateGiftCardRequest(
                null,
                0d,
                null,
                null,
                Set.of(newCustomerId)
        );

        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .build();
        exampleGiftCardEntity.setCreatedBy(createdByUsername);
        var oldCustomerId = UUID.randomUUID();
        var exampleCustomerEntity = UserEntity.builder().id(oldCustomerId).build();
        var exampleCustomerGiftCardEntity = CustomerGiftCardEntity.builder()
                .customer(exampleCustomerEntity)
                .giftCard(exampleGiftCardEntity)
                .build();
        var oldCustomerIdRedeemed = UUID.randomUUID();
        var exampleCustomerEntityRedeemed = UserEntity.builder().id(oldCustomerIdRedeemed).build();
        var exampleCustomerGiftCardEntityRedeemed = CustomerGiftCardEntity.builder()
                .customer(exampleCustomerEntityRedeemed)
                .giftCard(exampleGiftCardEntity)
                .build();
        exampleCustomerGiftCardEntityRedeemed.setRedeemed(true);
        exampleGiftCardEntity.setCustomers(Set.of(
                exampleCustomerGiftCardEntity,
                exampleCustomerGiftCardEntityRedeemed
        ));
        Mockito.when(giftCardRepository.findById(giftCardId))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        var newCustomerEntity = UserEntity.builder()
                .id(newCustomerId)
                .giftCards(new HashSet<>())
                .build();

        Mockito.when(userRepository.findById(newCustomerId))
                .thenReturn(Optional.of(newCustomerEntity));

        Mockito.when(customerGiftCardRepository.findById(Mockito.any(CustomerGiftCardId.class)))
                .thenReturn(Optional.empty());

        var exampleUserEntity = UserEntity.builder()
                .firstName(createdByFirstName)
                .lastName(createdByLastName)
                .phoneNumber(createdByUsername)
                .build();
        Mockito.when(userRepository.findByUsername(createdByUsername))
                .thenReturn(Optional.of(exampleUserEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        // when
        var result = giftCardService.update(giftCardId, request);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testUpdateHasEmptyOldCustomersSuccess() {
        // given
        var giftCardId = UUID.randomUUID();
        var newCustomerId = UUID.randomUUID();
        var createdByUsername = "0987654321";
        var createdByFirstName = "Created by first name";
        var createdByLastName = "Created by last name";
        var request = new UpdateGiftCardRequest(
                null,
                0d,
                null,
                null,
                Set.of(newCustomerId)
        );

        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .build();
        exampleGiftCardEntity.setCreatedBy(createdByUsername);
        exampleGiftCardEntity.setCustomers(Set.of());
        Mockito.when(giftCardRepository.findById(giftCardId))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        var newCustomerEntity = UserEntity.builder()
                .id(newCustomerId)
                .giftCards(new HashSet<>())
                .build();

        Mockito.when(userRepository.findById(newCustomerId))
                .thenReturn(Optional.of(newCustomerEntity));

        Mockito.when(customerGiftCardRepository.findById(Mockito.any(CustomerGiftCardId.class)))
                .thenReturn(Optional.of(new CustomerGiftCardEntity()));

        var exampleUserEntity = UserEntity.builder()
                .firstName(createdByFirstName)
                .lastName(createdByLastName)
                .phoneNumber(createdByUsername)
                .build();
        Mockito.when(userRepository.findByUsername(createdByUsername))
                .thenReturn(Optional.of(exampleUserEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        // when
        var result = giftCardService.update(giftCardId, request);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testUnlinkSuccess() {
        // given
        var notFoundId = UUID.randomUUID();
        var foundButRedeemedId = UUID.randomUUID();
        var foundId = UUID.randomUUID();
        var customerId = UUID.randomUUID();

        Mockito.when(customerGiftCardRepository.findById(Mockito.any(CustomerGiftCardId.class)))
                .thenAnswer((Answer<Optional<CustomerGiftCardEntity>>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();

                    var id = (CustomerGiftCardId) args[0];
                    var giftCardId = id.getGiftCardId();

                    if (notFoundId.equals(giftCardId)) {
                        return Optional.empty();
                    }

                    CustomerGiftCardEntity customerGiftCardEntity;
                    if (foundButRedeemedId.equals(giftCardId)) {
                        customerGiftCardEntity = CustomerGiftCardEntity.builder()
                                .giftCard(GiftCardEntity.builder().id(foundButRedeemedId).build())
                                .customer(UserEntity.builder().id(customerId).build())
                                .build();
                        customerGiftCardEntity.setRedeemed(true);
                        return Optional.of(customerGiftCardEntity);
                    }

                    customerGiftCardEntity = CustomerGiftCardEntity.builder()
                            .giftCard(GiftCardEntity.builder().id(foundId).build())
                            .customer(UserEntity.builder().id(customerId).build())
                            .build();

                    return Optional.of(customerGiftCardEntity);
                });

        // when
        var resultNotFound = giftCardService.unlink(notFoundId, customerId);
        var resultFoundRedeemed = giftCardService.unlink(foundButRedeemedId, customerId);
        var result = giftCardService.unlink(foundId, customerId);

        // then
        Assertions.assertThat(resultNotFound).isFalse();
        Assertions.assertThat(resultFoundRedeemed).isFalse();
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void testRedeemGiftCardIsNotRedeemableThenFailed() {
        try {
            // when
            giftCardService.redeem(UUID.randomUUID(), UUID.randomUUID());
        } catch (GiftCardIsNotRedeemableException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardIsNotRedeemableException.class);
        }
    }

    @Test
    void testRedeemCustomerNotFoundThenFailed() {
        try {
            // given
            var giftCardId = UUID.randomUUID();
            var customerId = UUID.randomUUID();
            var exampleGiftCardEntity = GiftCardEntity.builder()
                    .id(giftCardId)
                    .build();

            Mockito.when(giftCardRepository.findByIdAndStatus(giftCardId, GiftCardStatus.FULL))
                    .thenReturn(Optional.of(exampleGiftCardEntity));

            exampleGiftCardEntity.setStatus(GiftCardStatus.FULL);
            Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                    .thenReturn(exampleGiftCardEntity);

            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            // when
            giftCardService.redeem(giftCardId, customerId);
        } catch (CustomerNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Test
    void testRedeemGiftCardDoesNotBelongToCustomerThenFailed() {
        try {
            // given
            var giftCardId = UUID.randomUUID();
            var customerId = UUID.randomUUID();
            var exampleGiftCardEntity = GiftCardEntity.builder()
                    .id(giftCardId)
                    .build();

            Mockito.when(giftCardRepository.findByIdAndStatus(giftCardId, GiftCardStatus.FULL))
                    .thenReturn(Optional.of(exampleGiftCardEntity));

            exampleGiftCardEntity.setStatus(GiftCardStatus.FULL);
            Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                    .thenReturn(exampleGiftCardEntity);

            var exampleCustomerEntity = UserEntity.builder()
                    .id(customerId)
                    .build();
            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.of(exampleCustomerEntity));

            Mockito.when(customerGiftCardRepository.findById(Mockito.any(CustomerGiftCardId.class)))
                    .thenReturn(Optional.empty());

            // when
            giftCardService.redeem(giftCardId, customerId);
        } catch (GiftCardDoesNotBelongToCustomerException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardDoesNotBelongToCustomerException.class);
        }
    }

    @Test
    void testRedeemSuccess() {
        // given
        var giftCardId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .build();

        Mockito.when(giftCardRepository.findByIdAndStatus(giftCardId, GiftCardStatus.FULL))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        exampleGiftCardEntity.setStatus(GiftCardStatus.FULL);
        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        var exampleCustomerEntity = UserEntity.builder()
                .id(customerId)
                .build();
        Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleCustomerEntity));

        var exampleCustomerGiftCardEntity = CustomerGiftCardEntity.builder()
                .customer(exampleCustomerEntity)
                .giftCard(exampleGiftCardEntity)
                .build();
        Mockito.when(customerGiftCardRepository.findById(exampleCustomerGiftCardEntity.getId()))
                .thenReturn(Optional.of(exampleCustomerGiftCardEntity));

        exampleCustomerGiftCardEntity.setRedeemed(true);
        Mockito.when(giftCardHelper.redeemGiftCardForCustomer(exampleCustomerGiftCardEntity))
                .thenReturn(exampleCustomerGiftCardEntity);

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, false))
                .thenReturn(exampleGiftCardEntity);

        // when
        var result = giftCardService.redeem(giftCardId, customerId);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testSearchSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = GiftCardSortProperty.DATE_ISSUED;
        var requestNotEmptyButNotRedeemed = new SearchGiftCardRequest("TEST-NOT-EMPTY-BUT-NOT-REDEEMED", null);
        var requestEmpty = new SearchGiftCardRequest("TEST-EMPTY", null);
        var requestExpired = new SearchGiftCardRequest("TEST-EXPIRED", null);
        var pageable = PageRequest.of(page, size);
        var createdByUsername = "0987654321";

        var exampleGiftCardEntityNotRedeemed = GiftCardEntity.builder()
                .id(UUID.randomUUID())
                .status(GiftCardStatus.FULL)
                .build();

        var exampleGiftCardEntityExpired = GiftCardEntity.builder()
                .id(UUID.randomUUID())
                .status(GiftCardStatus.EXPIRED)
                .build();

        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(UUID.randomUUID())
                .status(GiftCardStatus.FULL)
                .build();

        Mockito.when(giftCardRepository.findAllGiftCards(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenAnswer((Answer<Page<GiftCardEntity>>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();

                    var criteria = (BooleanExpression) args[0];

                    exampleGiftCardEntity.setCreatedBy(createdByUsername);
                    exampleGiftCardEntityNotRedeemed.setCreatedBy(createdByUsername);
                    exampleGiftCardEntityExpired.setCreatedBy(createdByUsername);
                    if (StringUtils.containsIgnoreCase(criteria.toString(), requestNotEmptyButNotRedeemed.code())) {
                        var exampleCustomerGiftCardEntity = CustomerGiftCardEntity.builder()
                                .customer(UserEntity.builder().id(UUID.randomUUID()).build())
                                .giftCard(exampleGiftCardEntity)
                                .build();
                        exampleCustomerGiftCardEntity.setRedeemed(false);
                        exampleGiftCardEntityNotRedeemed.setCustomers(Set.of(exampleCustomerGiftCardEntity));
                        return new PageImpl<>(List.of(exampleGiftCardEntityNotRedeemed), pageable, 1);
                    }

                    if (StringUtils.containsIgnoreCase(criteria.toString(), requestExpired.code())) {
                        var exampleCustomerGiftCardEntity = CustomerGiftCardEntity.builder()
                                .customer(UserEntity.builder().id(UUID.randomUUID()).build())
                                .giftCard(exampleGiftCardEntityExpired)
                                .build();
                        exampleCustomerGiftCardEntity.setRedeemed(true);
                        exampleGiftCardEntityExpired.setCustomers(Set.of(exampleCustomerGiftCardEntity));
                        return new PageImpl<>(List.of(exampleGiftCardEntityExpired), pageable, 1);
                    }

                    return new PageImpl<>(List.of(exampleGiftCardEntity), pageable, 1);
                });

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(Mockito.any(GiftCardEntity.class), Mockito.eq(false)))
                .thenAnswer((Answer<GiftCardEntity>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();

                    var giftCard = (GiftCardEntity) args[0];
                    var giftCardId = giftCard.getId();

                    if (exampleGiftCardEntityNotRedeemed.getId().equals(giftCardId)) {
                        return exampleGiftCardEntityNotRedeemed;
                    }

                    if (exampleGiftCardEntityExpired.getId().equals(giftCardId)) {
                        return exampleGiftCardEntityExpired;
                    }

                    return exampleGiftCardEntity;
                });

        // when
        var resultNotEmptyButNotRedeemed = giftCardService.search(page, size, direction, property, requestNotEmptyButNotRedeemed);
        var resultEmpty = giftCardService.search(page, size, direction, property, requestEmpty);
        var resultExpired = giftCardService.search(page, size, direction, property, requestExpired);

        // then
        Assertions.assertThat(resultNotEmptyButNotRedeemed).isNotNull();
        Assertions.assertThat(resultEmpty).isNotNull();
        Assertions.assertThat(resultExpired).isNotNull();
    }
}
