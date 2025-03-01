package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditRequest;
import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import com.hesmantech.salonbooking.base.AbstractUserTests;
import com.hesmantech.salonbooking.domain.CreditConfigurationEntity;
import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.credit.CreditNotFoundException;
import com.hesmantech.salonbooking.exception.creditconfiguration.CreditConfigurationNotFoundException;
import com.hesmantech.salonbooking.repository.CreditConfigurationRepository;
import com.hesmantech.salonbooking.repository.CreditRepository;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.service.impl.CreditServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreditServiceTests extends AbstractUserTests {
    @Mock
    private CreditRepository creditRepository;

    @Mock
    private CreditConfigurationRepository creditConfigurationRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerGiftCardRepository customerGiftCardRepository;

    @Mock
    private GiftCardRepository giftCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreditService creditService;

    private UserEntity exampleCustomer;
    private CreditConfigurationEntity exampleCreditConfiguration;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.creditService = new CreditServiceImpl(
                creditRepository,
                creditConfigurationRepository,
                orderRepository,
                customerGiftCardRepository,
                giftCardRepository,
                userRepository
        );

        this.exampleCustomer = new UserEntity();
        var customerId = UUID.randomUUID();
        exampleCustomer.setId(customerId);

        this.exampleCreditConfiguration = CreditConfigurationEntity.builder()
                .conversionCredit(1d)
                .creditThreshold(600d)
                .build();
    }

    @Test
    @DisplayName("Update customer credit point when customer check out")
    void testUpdateBasedOnCustomerCheckOut() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setCustomer(exampleCustomer);
        setUpStubs();
        var customerId = exampleCustomer.getId();
        var exampleCustomerCredit = CreditEntity.builder()
                .id(customerId)
                .customer(exampleCustomer)
                .availableCredit(600d)
                .totalCredit(1200d)
                .availableNoGiftCard(1)
                .redeemNoGiftCard(1)
                .build();
        exampleCustomerCredit.setCreatedBy(ADMIN_USERNAME);
        exampleCustomerCredit.setLastModifiedBy(ADMIN_USERNAME);
        Mockito.when(creditRepository.save(Mockito.any(CreditEntity.class)))
                .thenReturn(exampleCustomerCredit);

        Mockito.when(creditRepository.findById(customerId))
                .thenReturn(Optional.of(exampleCustomerCredit));

        // when
        creditService.updateBasedOn(orderEntity);

        // then
        var result = creditRepository.findById(exampleCustomer.getId());
        Assertions.assertThat(result).isPresent();

        var creditEntity = result.get();
        Assertions.assertThat(creditEntity.getId()).isEqualTo(exampleCustomer.getId());
        Assertions.assertThat(creditEntity.getCustomer()).isEqualTo(exampleCustomer);
        Assertions.assertThat(creditEntity.getAvailableCredit()).isEqualTo(600d);
        Assertions.assertThat(creditEntity.getTotalCredit()).isEqualTo(1200d);
        Assertions.assertThat(creditEntity.getAvailableNoGiftCard()).isEqualTo(1);
        Assertions.assertThat(creditEntity.getRedeemNoGiftCard()).isEqualTo(1);
        Assertions.assertThat(creditEntity.getTotalNoGiftCard()).isEqualTo(2);
    }

    @Test
    @DisplayName("Update customer credit point when customer redeem")
    void testUpdateBasedOnCustomerRedeem() {
        // given
        var giftCardEntity = new GiftCardEntity();
        giftCardEntity.setId(UUID.randomUUID());
        giftCardEntity.setCustomers(Set.of(CustomerGiftCardEntity.builder()
                .giftCard(giftCardEntity)
                .customer(exampleCustomer)
                .build()));
        setUpStubs();
        var customerId = exampleCustomer.getId();
        var exampleCustomerCredit = CreditEntity.builder()
                .id(customerId)
                .customer(exampleCustomer)
                .availableCredit(600d)
                .totalCredit(1200d)
                .availableNoGiftCard(1)
                .redeemNoGiftCard(1)
                .build();
        exampleCustomerCredit.setCreatedBy(ADMIN_USERNAME);
        exampleCustomerCredit.setLastModifiedBy(ADMIN_USERNAME);
        Mockito.when(creditRepository.save(Mockito.any(CreditEntity.class)))
                .thenReturn(exampleCustomerCredit);

        Mockito.when(creditRepository.findById(customerId))
                .thenReturn(Optional.of(exampleCustomerCredit));

        // when
        creditService.updateBasedOn(giftCardEntity);

        // then
        var result = creditRepository.findById(exampleCustomer.getId());
        Assertions.assertThat(result).isPresent();

        var creditEntity = result.get();
        Assertions.assertThat(creditEntity.getId()).isEqualTo(exampleCustomer.getId());
        Assertions.assertThat(creditEntity.getCustomer()).isEqualTo(exampleCustomer);
        Assertions.assertThat(creditEntity.getAvailableCredit()).isEqualTo(600d);
        Assertions.assertThat(creditEntity.getTotalCredit()).isEqualTo(1200d);
        Assertions.assertThat(creditEntity.getAvailableNoGiftCard()).isEqualTo(1);
        Assertions.assertThat(creditEntity.getRedeemNoGiftCard()).isEqualTo(1);
        Assertions.assertThat(creditEntity.getTotalNoGiftCard()).isEqualTo(2);
    }

    @Test
    @DisplayName("When credit configuration not found then throw exception")
    void testCreditConfigurationNotFound() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setCustomer(exampleCustomer);
        Mockito.when(creditConfigurationRepository.findTopByOrderByCreatedDateDesc())
                .thenReturn(Optional.empty());

        // when

        // then
        org.junit.jupiter.api.Assertions.assertThrows(CreditConfigurationNotFoundException.class,
                () -> creditService.updateBasedOn(orderEntity));
    }

    @Test
    @DisplayName("Get a customer credit details")
    void testGetCustomerCreditDetails() {
        // given
        setUpStubs();
        var customerId = exampleCustomer.getId();
        var exampleCustomerCredit = CreditEntity.builder()
                .id(customerId)
                .customer(exampleCustomer)
                .availableCredit(600d)
                .totalCredit(1200d)
                .availableNoGiftCard(1)
                .redeemNoGiftCard(1)
                .build();
        exampleCustomerCredit.setCreatedBy(ADMIN_USERNAME);
        exampleCustomerCredit.setLastModifiedBy(ADMIN_USERNAME);
        Mockito.when(creditRepository.save(Mockito.any(CreditEntity.class)))
                .thenReturn(exampleCustomerCredit);

        Mockito.when(creditRepository.findById(customerId))
                .thenReturn(Optional.of(exampleCustomerCredit));

        Mockito.when(userRepository.findByUsername(ADMIN_USERNAME))
                .thenReturn(Optional.of(exampleUser));

        // when
        var creditEntity = creditService.getDetails(exampleCustomer.getId());

        // then
        Assertions.assertThat(creditEntity).isNotNull();
        Assertions.assertThat(creditEntity.getCreatedBy()).isEqualTo("%s %s", ADMIN_FIRST_NAME, ADMIN_LAST_NAME);
        Assertions.assertThat(creditEntity.getLastModifiedBy()).isEqualTo("%s %s", ADMIN_FIRST_NAME, ADMIN_LAST_NAME);
    }

    @Test
    @DisplayName("When customer credit not found then throw exception")
    void testGetCustomerCreditNotFound() {
        // given
        var customerId = exampleCustomer.getId();
        Mockito.when(creditRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // when

        // then
        org.junit.jupiter.api.Assertions.assertThrows(CreditNotFoundException.class,
                () -> creditService.getDetails(customerId));
    }

    private void setUpStubs() {
        var customerId = exampleCustomer.getId();
        Mockito.when(creditConfigurationRepository.findTopByOrderByCreatedDateDesc())
                .thenReturn(Optional.of(exampleCreditConfiguration));

        Mockito.when(orderRepository.totalPriceByCustomerIdAndCustomerStatusIn(customerId, List.of(UserStatus.ACTIVE)))
                .thenReturn(1200d);

        Mockito.when(customerGiftCardRepository.countByCustomerIdAndRedeemedIsTrue(customerId))
                .thenReturn(1);

        Mockito.when(giftCardRepository.countAvailableNoGiftCard(customerId))
                .thenReturn(1);

        Mockito.when(giftCardRepository.countTotalNoGiftCard(customerId))
                .thenReturn(2);
    }

    @Test
    @DisplayName("Update customer credit with unsupported entity")
    void testUpdateCustomerCreditWithUnsupportedEntity() {
        // given
        var customer = new UserEntity();

        var creditServiceSpy = Mockito.spy(creditService);

        // when
        creditServiceSpy.updateBasedOn(customer);

        // then
        Mockito.verify(creditServiceSpy, Mockito.times(1)).updateBasedOn(customer);
    }

    @Test
    @DisplayName("When gift card has empty customer then customer is null")
    void testGiftCardHasEmptyCustomer() {
        // given
        var giftCard = new GiftCardEntity();
        giftCard.setCustomers(Set.of());
        Mockito.when(creditConfigurationRepository.findTopByOrderByCreatedDateDesc())
                .thenReturn(Optional.of(exampleCreditConfiguration));

        var creditServiceSpy = Mockito.spy(creditService);

        // when
        creditServiceSpy.updateBasedOn(giftCard);

        // then
        Mockito.verify(creditServiceSpy, Mockito.times(1)).updateBasedOn(giftCard);
    }

    @Test
    @DisplayName("When gift card has more than 1 customer then customer is null")
    void testGiftCardHasMoreThan1Customer() {
        // given
        var giftCard = new GiftCardEntity();
        giftCard.setId(UUID.randomUUID());

        var customer1 = new UserEntity();
        customer1.setId(UUID.randomUUID());

        var customer2 = new UserEntity();
        customer2.setId(UUID.randomUUID());

        giftCard.setCustomers(Set.of(new CustomerGiftCardEntity(customer1, giftCard),
                new CustomerGiftCardEntity(customer2, giftCard)));
        Mockito.when(creditConfigurationRepository.findTopByOrderByCreatedDateDesc())
                .thenReturn(Optional.of(exampleCreditConfiguration));

        var creditServiceSpy = Mockito.spy(creditService);

        // when
        creditServiceSpy.updateBasedOn(giftCard);

        // then
        Mockito.verify(creditServiceSpy, Mockito.times(1)).updateBasedOn(giftCard);
    }

    @Test
    @DisplayName("When the first time credit balance been created")
    void testCreditBalanceCreated() {
        // given
        var orderEntity = new OrderEntity();
        orderEntity.setCustomer(exampleCustomer);
        setUpStubs();
        Mockito.when(creditRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        var creditServiceSpy = Mockito.spy(creditService);

        // when
        creditServiceSpy.updateBasedOn(orderEntity);

        // then
        Mockito.verify(creditServiceSpy, Mockito.times(1)).updateBasedOn(orderEntity);
    }

    @Test
    @DisplayName("Search credit success")
    void testSearchCreditSuccess() {
        // given
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.DESC;
        CustomerCreditSortProperty property = CustomerCreditSortProperty.CREATED_DATE;
        SearchCustomerCreditRequest request = new SearchCustomerCreditRequest(null);
        setUpStubs();

        var customerId = exampleCustomer.getId();
        var exampleCustomerCredit = CreditEntity.builder()
                .id(customerId)
                .customer(exampleCustomer)
                .availableCredit(600d)
                .totalCredit(1200d)
                .availableNoGiftCard(1)
                .redeemNoGiftCard(1)
                .build();
        exampleCustomerCredit.setCreatedBy(ADMIN_USERNAME);
        exampleCustomerCredit.setLastModifiedBy(ADMIN_USERNAME);
        Mockito.when(creditRepository.save(Mockito.any(CreditEntity.class)))
                .thenReturn(exampleCustomerCredit);

        Mockito.when(creditRepository.findAllCredits(Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(exampleCustomerCredit)));

        // when
        var result = creditService.search(page, size, direction, property, request);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Search credit with criteria success")
    void testSearchCreditWithCriteriaSuccess() {
        // given
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.DESC;
        CustomerCreditSortProperty property = CustomerCreditSortProperty.CREATED_DATE;
        SearchCustomerCreditRequest request = new SearchCustomerCreditRequest(ADMIN_FIRST_NAME);
        setUpStubs();

        var customerId = exampleCustomer.getId();
        var exampleCustomerCredit = CreditEntity.builder()
                .id(customerId)
                .customer(exampleCustomer)
                .availableCredit(600d)
                .totalCredit(1200d)
                .availableNoGiftCard(1)
                .redeemNoGiftCard(1)
                .build();
        exampleCustomerCredit.setCreatedBy(ADMIN_USERNAME);
        exampleCustomerCredit.setLastModifiedBy(ADMIN_USERNAME);
        Mockito.when(creditRepository.save(Mockito.any(CreditEntity.class)))
                .thenReturn(exampleCustomerCredit);

        Mockito.when(creditRepository.findAllCredits(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(exampleCustomerCredit)));

        // when
        var result = creditService.search(page, size, direction, property, request);

        // then
        Assertions.assertThat(result).isNotNull();
    }
}
