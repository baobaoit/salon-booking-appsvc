package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.giftcardbalance.RedeemByCodeRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcardbalance.GCBActivitySortProperty;
import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.CustomerGiftCardId;
import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardDoesNotBelongToCustomerException;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardIsNotRedeemableException;
import com.hesmantech.salonbooking.exception.giftcardbalance.GiftCardBalanceIsNotEnoughToPayException;
import com.hesmantech.salonbooking.exception.order.OrderTotalPriceInvalidException;
import com.hesmantech.salonbooking.exception.user.CustomerNotFoundException;
import com.hesmantech.salonbooking.helper.giftcard.GiftCardHelper;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardBalanceActivityRepository;
import com.hesmantech.salonbooking.repository.GiftCardBalanceRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.service.impl.GiftCardBalanceServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GiftCardBalanceServiceTests {
    @Mock
    private GiftCardBalanceRepository giftCardBalanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GiftCardRepository giftCardRepository;

    @Mock
    private CustomerGiftCardRepository customerGiftCardRepository;

    @Mock
    private GiftCardBalanceActivityRepository giftCardBalanceActivityRepository;

    @Mock
    private GiftCardHelper giftCardHelper;

    private GiftCardBalanceService giftCardBalanceService;

    @BeforeEach
    void setUp() {
        this.giftCardBalanceService = new GiftCardBalanceServiceImpl(
                giftCardBalanceRepository,
                userRepository,
                giftCardRepository,
                customerGiftCardRepository,
                giftCardBalanceActivityRepository,
                giftCardHelper
        );
    }

    @Test
    void testGetLatestGiftCardBalanceFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            // when
            giftCardBalanceService.getLatestGiftCardBalance(customerId);
        } catch (CustomerNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Test
    void testGetLatestGiftCardBalanceSuccess() {
        // given
        var customerIdNotHasGiftBalance = UUID.randomUUID();
        var customerIdHasGiftBalance = UUID.randomUUID();

        Mockito.when(userRepository.findByIdAndStatus(customerIdNotHasGiftBalance, UserStatus.ACTIVE))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(userRepository.findByIdAndStatus(customerIdHasGiftBalance, UserStatus.ACTIVE))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(giftCardBalanceRepository.findById(Mockito.any(UUID.class)))
                .thenAnswer((Answer<Optional<GiftCardBalanceEntity>>) invocationOnMock -> {
                    Object[] args = invocationOnMock.getArguments();

                    var customerId = (UUID) args[0];

                    if (customerIdNotHasGiftBalance.equals(customerId)) {
                        return Optional.empty();
                    }

                    return Optional.of(new GiftCardBalanceEntity() {{
                        setBalance(1d);
                    }});
                });

        // when
        var resultEmpty = giftCardBalanceService.getLatestGiftCardBalance(customerIdNotHasGiftBalance);
        var resultHasBalance = giftCardBalanceService.getLatestGiftCardBalance(customerIdHasGiftBalance);

        // then
        Assertions.assertThat(resultEmpty).isNotNull();
        Assertions.assertThat(resultHasBalance).isNotNull();
        Assertions.assertThat(resultEmpty.getBalance()).isEqualTo(0d);
        Assertions.assertThat(resultHasBalance.getBalance()).isEqualTo(1d);
    }

    @Test
    void testRedeemGiftCardCodeFailed() {
        try {
            // given
            var giftCode = "TEST";
            var request = new RedeemByCodeRequest(giftCode);

            Mockito.when(giftCardRepository.findByCodeAndStatus(giftCode, GiftCardStatus.FULL))
                    .thenReturn(Optional.empty());

            // when
            giftCardBalanceService.redeemGiftCardCode(null, request);
        } catch (GiftCardIsNotRedeemableException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardIsNotRedeemableException.class);
        }
    }

    @Test
    void testRedeemGiftCardCodeCustomerNotFoundFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var giftCode = "TEST";
            var request = new RedeemByCodeRequest(giftCode);

            var exampleGiftCardEntity = GiftCardEntity.builder()
                    .code(giftCode)
                    .status(GiftCardStatus.FULL)
                    .build();
            Mockito.when(giftCardRepository.findByCodeAndStatus(giftCode, GiftCardStatus.FULL))
                    .thenReturn(Optional.of(exampleGiftCardEntity));

            Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                    .thenReturn(exampleGiftCardEntity);

            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            // when
            giftCardBalanceService.redeemGiftCardCode(customerId, request);
        } catch (CustomerNotFoundException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(CustomerNotFoundException.class);
        }
    }

    @Test
    void testRedeemGiftCardCodeCustomerNotOwnerGiftCardFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var giftCardId = UUID.randomUUID();
            var giftCode = "TEST";
            var request = new RedeemByCodeRequest(giftCode);

            var exampleGiftCardEntity = GiftCardEntity.builder()
                    .id(giftCardId)
                    .code(giftCode)
                    .status(GiftCardStatus.FULL)
                    .build();
            Mockito.when(giftCardRepository.findByCodeAndStatus(giftCode, GiftCardStatus.FULL))
                    .thenReturn(Optional.of(exampleGiftCardEntity));

            Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                    .thenReturn(exampleGiftCardEntity);

            var exampleCustomerEntity = UserEntity.builder()
                    .id(customerId)
                    .firstName("First name")
                    .lastName("Last name")
                    .build();
            Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                    .thenReturn(Optional.of(exampleCustomerEntity));

            Mockito.when(customerGiftCardRepository.findById(CustomerGiftCardId.builder()
                            .customerId(customerId)
                            .giftCardId(giftCardId)
                            .build()))
                    .thenReturn(Optional.empty());

            // when
            giftCardBalanceService.redeemGiftCardCode(customerId, request);
        } catch (GiftCardDoesNotBelongToCustomerException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardDoesNotBelongToCustomerException.class);
        }
    }

    @Test
    void testRedeemGiftCardCodeSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var giftCardId = UUID.randomUUID();
        var giftCode = "TEST";
        var request = new RedeemByCodeRequest(giftCode);

        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .code(giftCode)
                .status(GiftCardStatus.FULL)
                .initialValue(1d)
                .build();
        Mockito.when(giftCardRepository.findByCodeAndStatus(giftCode, GiftCardStatus.FULL))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        var exampleCustomerEntity = UserEntity.builder()
                .id(customerId)
                .firstName("First name")
                .lastName("Last name")
                .build();
        Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleCustomerEntity));

        var exampleCustomerGiftCardEntity = new CustomerGiftCardEntity(exampleCustomerEntity, exampleGiftCardEntity);
        exampleCustomerGiftCardEntity.setRedeemed(true);
        Mockito.when(customerGiftCardRepository.findById(CustomerGiftCardId.builder()
                        .customerId(customerId)
                        .giftCardId(giftCardId)
                        .build()))
                .thenReturn(Optional.of(exampleCustomerGiftCardEntity));

        Mockito.when(giftCardHelper.redeemGiftCardForCustomer(exampleCustomerGiftCardEntity))
                .thenReturn(exampleCustomerGiftCardEntity);

        Mockito.when(giftCardBalanceRepository.findById(customerId))
                .thenReturn(Optional.empty());

        Mockito.when(giftCardBalanceRepository.save(Mockito.any(GiftCardBalanceEntity.class)))
                .thenReturn(new GiftCardBalanceEntity());

        // when
        var result = giftCardBalanceService.redeemGiftCardCode(customerId, request);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void testRedeemGiftCardCodeCustomerHasGiftBalanceSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var giftCardId = UUID.randomUUID();
        var giftCode = "TEST";
        var request = new RedeemByCodeRequest(giftCode);

        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(giftCardId)
                .code(giftCode)
                .status(GiftCardStatus.FULL)
                .initialValue(1d)
                .build();
        Mockito.when(giftCardRepository.findByCodeAndStatus(giftCode, GiftCardStatus.FULL))
                .thenReturn(Optional.of(exampleGiftCardEntity));

        Mockito.when(giftCardHelper.updateGiftCardStatusAndSave(exampleGiftCardEntity, true))
                .thenReturn(exampleGiftCardEntity);

        var exampleCustomerEntity = UserEntity.builder()
                .id(customerId)
                .firstName("First name")
                .lastName("Last name")
                .build();
        Mockito.when(userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE))
                .thenReturn(Optional.of(exampleCustomerEntity));

        var exampleCustomerGiftCardEntity = new CustomerGiftCardEntity(exampleCustomerEntity, exampleGiftCardEntity);
        exampleCustomerGiftCardEntity.setRedeemed(true);
        Mockito.when(customerGiftCardRepository.findById(CustomerGiftCardId.builder()
                        .customerId(customerId)
                        .giftCardId(giftCardId)
                        .build()))
                .thenReturn(Optional.of(exampleCustomerGiftCardEntity));

        Mockito.when(giftCardHelper.redeemGiftCardForCustomer(exampleCustomerGiftCardEntity))
                .thenReturn(exampleCustomerGiftCardEntity);

        var exampleGiftCardBalanceEntity = new GiftCardBalanceEntity();
        exampleGiftCardBalanceEntity.setCustomer(exampleCustomerEntity);
        Mockito.when(giftCardBalanceRepository.findById(customerId))
                .thenReturn(Optional.of(exampleGiftCardBalanceEntity));

        Mockito.when(giftCardBalanceRepository.save(Mockito.any(GiftCardBalanceEntity.class)))
                .thenReturn(exampleGiftCardBalanceEntity);

        // when
        var result = giftCardBalanceService.redeemGiftCardCode(customerId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getBalance()).isEqualTo(1d);
    }

    @Test
    void testUpdateWhenRedeemGiftCardSuccess() {
        // given
        var customerId = UUID.randomUUID();
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(UUID.randomUUID())
                .code("TEST")
                .status(GiftCardStatus.FULL)
                .initialValue(1d)
                .build();
        var exampleCustomerEntity = UserEntity.builder()
                .id(customerId)
                .build();
        var customers = Set.of(CustomerGiftCardEntity.builder()
                .customer(exampleCustomerEntity)
                .giftCard(exampleGiftCardEntity)
                .build());
        exampleGiftCardEntity.setCustomers(customers);

        var exampleGiftCardBalanceEntity = new GiftCardBalanceEntity();
        exampleGiftCardBalanceEntity.setCustomer(exampleCustomerEntity);
        Mockito.when(giftCardBalanceRepository.findById(customerId))
                .thenReturn(Optional.of(exampleGiftCardBalanceEntity));

        Mockito.when(giftCardBalanceRepository.save(Mockito.any(GiftCardBalanceEntity.class)))
                .thenReturn(exampleGiftCardBalanceEntity);

        var serviceSpy = Mockito.spy(giftCardBalanceService);

        // when
        serviceSpy.updateWhenRedeemGiftCard(exampleGiftCardEntity);

        // then
        Mockito.verify(serviceSpy, Mockito.times(1))
                .updateWhenRedeemGiftCard(exampleGiftCardEntity);
    }

    @Test
    void testUpdateWhenRedeemGiftCardHasCustomersEmptyThenFailed() {
        // given
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(UUID.randomUUID())
                .code("TEST")
                .status(GiftCardStatus.FULL)
                .initialValue(1d)
                .customers(Set.of())
                .build();
        var customers = Set.of(
                CustomerGiftCardEntity.builder()
                        .customer(UserEntity.builder().id(UUID.randomUUID()).build())
                        .giftCard(exampleGiftCardEntity)
                        .build(),
                CustomerGiftCardEntity.builder()
                        .customer(UserEntity.builder().id(UUID.randomUUID()).build())
                        .giftCard(exampleGiftCardEntity)
                        .build()
        );
        exampleGiftCardEntity.setCustomers(customers);

        var serviceSpy = Mockito.spy(giftCardBalanceService);

        // when
        serviceSpy.updateWhenRedeemGiftCard(exampleGiftCardEntity);

        // then
        Mockito.verify(serviceSpy, Mockito.times(1))
                .updateWhenRedeemGiftCard(exampleGiftCardEntity);
    }

    @Test
    void testUpdateWhenRedeemGiftCardHasCustomersMoreThanOneThenFailed() {
        // given
        var exampleGiftCardEntity = GiftCardEntity.builder()
                .id(UUID.randomUUID())
                .code("TEST")
                .status(GiftCardStatus.FULL)
                .initialValue(1d)
                .build();

        var serviceSpy = Mockito.spy(giftCardBalanceService);

        // when
        serviceSpy.updateWhenRedeemGiftCard(exampleGiftCardEntity);

        // then
        Mockito.verify(serviceSpy, Mockito.times(1))
                .updateWhenRedeemGiftCard(exampleGiftCardEntity);
    }

    @ParameterizedTest
    @CsvSource({
            "1.0, 0.0, 0.0, 1.0",
            "2.0, 0.0, 1.0, 1.0",
            "2.0, 0.0, 0.0, 3.0"
    })
    void testUpdateWhenCheckOutOrderSuccess(double subtotal, double discount, double price, double balance) {
        // given
        var customerId = UUID.randomUUID();
        var customer = UserEntity.builder().id(customerId).build();
        var exampleOrderEntity = OrderEntity.builder()
                .customer(customer)
                .subtotal(subtotal)
                .discount(discount)
                .price(price)
                .build();

        var exampleGiftCardBalanceEntity = GiftCardBalanceEntity.builder()
                .customer(customer)
                .balance(balance)
                .activities(new ArrayList<>())
                .build();
        Mockito.when(giftCardBalanceRepository.findById(customerId))
                .thenReturn(Optional.of(exampleGiftCardBalanceEntity));

        Mockito.when(giftCardBalanceRepository.save(Mockito.any(GiftCardBalanceEntity.class)))
                .thenReturn(exampleGiftCardBalanceEntity);

        var serviceSpy = Mockito.spy(giftCardBalanceService);

        // when
        serviceSpy.updateWhenCheckOutOrder(exampleOrderEntity);

        // then
        Mockito.verify(serviceSpy, Mockito.times(1))
                .updateWhenCheckOutOrder(exampleOrderEntity);
    }

    @Test
    void testUpdateWhenCheckOutOrderTotalPriceGreaterThanThresholdThenFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var customer = UserEntity.builder().id(customerId).build();
            var exampleOrderEntity = OrderEntity.builder()
                    .customer(customer)
                    .subtotal(1d)
                    .discount(0d)
                    .price(1d)
                    .build();

            var exampleGiftCardBalanceEntity = GiftCardBalanceEntity.builder()
                    .customer(customer)
                    .balance(1d)
                    .activities(new ArrayList<>())
                    .build();
            Mockito.when(giftCardBalanceRepository.findById(customerId))
                    .thenReturn(Optional.of(exampleGiftCardBalanceEntity));

            // when
            giftCardBalanceService.updateWhenCheckOutOrder(exampleOrderEntity);
        } catch (OrderTotalPriceInvalidException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderTotalPriceInvalidException.class);
        }
    }

    @Test
    void testUpdateWhenCheckOutOrderGreaterThanThresholdThenFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var customer = UserEntity.builder().id(customerId).build();
            var exampleOrderEntity = OrderEntity.builder()
                    .customer(customer)
                    .subtotal(3d)
                    .discount(0d)
                    .price(1d)
                    .build();

            var exampleGiftCardBalanceEntity = GiftCardBalanceEntity.builder()
                    .customer(customer)
                    .balance(1d)
                    .activities(new ArrayList<>())
                    .build();
            Mockito.when(giftCardBalanceRepository.findById(customerId))
                    .thenReturn(Optional.of(exampleGiftCardBalanceEntity));

            // when
            giftCardBalanceService.updateWhenCheckOutOrder(exampleOrderEntity);
        } catch (OrderTotalPriceInvalidException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(OrderTotalPriceInvalidException.class);
        }
    }

    @Test
    void testUpdateWhenCheckOutOrderGiftBalanceNotEnoughToPayThenFailed() {
        try {
            // given
            var customerId = UUID.randomUUID();
            var customer = UserEntity.builder().id(customerId).build();
            var exampleOrderEntity = OrderEntity.builder()
                    .customer(customer)
                    .subtotal(3d)
                    .discount(0d)
                    .price(1d)
                    .build();

            var exampleGiftCardBalanceEntity = GiftCardBalanceEntity.builder()
                    .customer(customer)
                    .activities(new ArrayList<>())
                    .build();
            Mockito.when(giftCardBalanceRepository.findById(customerId))
                    .thenReturn(Optional.of(exampleGiftCardBalanceEntity));

            // when
            giftCardBalanceService.updateWhenCheckOutOrder(exampleOrderEntity);
        } catch (GiftCardBalanceIsNotEnoughToPayException e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardBalanceIsNotEnoughToPayException.class);
        }
    }

    @Test
    void testSearchSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = GCBActivitySortProperty.CREATED_DATE;
        var pageRequest = PageRequest.of(page, size);
        var requestEmpty = new SearchGCBActivityRequest(null);
        var customerId = UUID.randomUUID();
        var requestCustomerId = new SearchGCBActivityRequest(customerId);

        Mockito.when(giftCardBalanceActivityRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageRequest, 0));

        Mockito.when(giftCardBalanceActivityRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageRequest, 0));

        // when
        var resultEmpty = giftCardBalanceService.search(page, size, direction, property, requestEmpty);
        var resultCustomerId = giftCardBalanceService.search(page, size, direction, property, requestCustomerId);

        // then
        Assertions.assertThat(resultEmpty).isNotNull();
        Assertions.assertThat(resultEmpty.getContent()).isEmpty();
        Assertions.assertThat(resultEmpty.getPageable()).isNotNull();
        var pageableEmpty = resultEmpty.getPageable();
        Assertions.assertThat(pageableEmpty.getPageNumber()).isEqualTo(page);
        Assertions.assertThat(pageableEmpty.getPageSize()).isEqualTo(size);

        Assertions.assertThat(resultCustomerId).isNotNull();
        Assertions.assertThat(resultCustomerId.getContent()).isEmpty();
        Assertions.assertThat(resultCustomerId.getPageable()).isNotNull();
        var pageableCustomerId = resultCustomerId.getPageable();
        Assertions.assertThat(pageableCustomerId.getPageNumber()).isEqualTo(page);
        Assertions.assertThat(pageableCustomerId.getPageSize()).isEqualTo(size);

    }
}
