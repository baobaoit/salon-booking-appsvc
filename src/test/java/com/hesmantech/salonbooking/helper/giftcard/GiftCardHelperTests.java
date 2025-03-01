package com.hesmantech.salonbooking.helper.giftcard;

import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardAlreadyRedeemedException;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class GiftCardHelperTests {
    @Mock
    private CustomerGiftCardRepository customerGiftCardRepository;

    @Mock
    private GiftCardRepository giftCardRepository;

    private GiftCardHelper giftCardHelper;

    @BeforeEach
    void setUp() {
        this.giftCardHelper = new GiftCardHelperImpl(
                customerGiftCardRepository,
                giftCardRepository
        );
    }

    @Test
    @DisplayName("Customer redeem gift card success")
    void testCustomerRedeemGiftCardSuccess() {
        // given
        var customerGiftCard = new CustomerGiftCardEntity();
        customerGiftCard.setRedeemed(false);

        var exampleCustomerGiftCard = new CustomerGiftCardEntity();
        exampleCustomerGiftCard.setRedeemed(true);
        Mockito.when(customerGiftCardRepository.save(Mockito.any(CustomerGiftCardEntity.class)))
                .thenReturn(exampleCustomerGiftCard);

        // when
        var result = giftCardHelper.redeemGiftCardForCustomer(customerGiftCard);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.isRedeemed()).isTrue();
    }

    @Test
    @DisplayName("Customer try redeem a gift card multiple times")
    void testCustomerRedeemGiftCardMultipleTimes() {
        try {
            // given
            var customerGiftCard = new CustomerGiftCardEntity();
            customerGiftCard.setRedeemed(true);

            var giftCard = GiftCardEntity.builder()
                    .code("T3S7C0D3")
                    .build();
            customerGiftCard.setGiftCard(giftCard);

            // when
            giftCardHelper.redeemGiftCardForCustomer(customerGiftCard);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(GiftCardAlreadyRedeemedException.class);
        }
    }

    @Test
    @DisplayName("Update gift card status (full) and save success")
    void testUpdateGiftCardStatusFullAndSaveSuccess() {
        // given
        var today = Instant.now();
        var zdt = today.atZone(ZoneId.systemDefault()).plusWeeks(1);
        var weekLater = zdt.toInstant();
        var giftCard = GiftCardEntity.builder()
                .initialValue(10d)
                .customers(Set.of(new CustomerGiftCardEntity()))
                .expirationDate(weekLater)
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.FULL);
    }

    @Test
    @DisplayName("Update gift card status (full - not have expiration date) and save success")
    void testUpdateGiftCardStatusFullNotHaveExpirationDateAndSaveSuccess() {
        // given
        var giftCard = GiftCardEntity.builder()
                .initialValue(10d)
                .customers(Set.of(new CustomerGiftCardEntity()))
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.FULL);
    }

    @Test
    @DisplayName("Update gift card status (expired) and save success")
    void testUpdateGiftCardStatusExpiredAndSaveSuccess() {
        // given
        var today = Instant.now();
        var zdt = today.atZone(ZoneId.systemDefault()).minusWeeks(1);
        var weekBefore = zdt.toInstant();
        var giftCard = GiftCardEntity.builder()
                .initialValue(10d)
                .customers(Set.of(new CustomerGiftCardEntity()))
                .expirationDate(weekBefore)
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.EXPIRED);
    }

    @Test
    @DisplayName("Update gift card status (partial - not have customers) and save success")
    void testUpdateGiftCardStatusPartialNotHaveCustomersAndSaveSuccess() {
        // given
        var giftCard = GiftCardEntity.builder()
                .initialValue(10d)
                .customers(Set.of())
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.PARTIAL);
    }

    @Test
    @DisplayName("Update gift card status (partial - not have initial value) and save success")
    void testUpdateGiftCardStatusPartialNotHaveInitialValueAndSaveSuccess() {
        // given
        var giftCard = GiftCardEntity.builder()
                .initialValue(0d)
                .customers(Set.of(new CustomerGiftCardEntity()))
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.PARTIAL);
    }

    @Test
    @DisplayName("Update gift card status (partial - have expiration date) and save success")
    void testUpdateGiftCardStatusPartialHaveExpirationDateAndSaveSuccess() {
        // given
        var today = Instant.now();
        var zdt = today.atZone(ZoneId.systemDefault()).plusWeeks(1);
        var weekLater = zdt.toInstant();
        var giftCard = GiftCardEntity.builder()
                .initialValue(0d)
                .customers(Set.of(new CustomerGiftCardEntity()))
                .expirationDate(weekLater)
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.PARTIAL);
    }

    @Test
    @DisplayName("Update gift card status (partial - but expired) and save success")
    void testUpdateGiftCardStatusPartialButExpiredAndSaveSuccess() {
        // given
        var today = Instant.now();
        var zdt = today.atZone(ZoneId.systemDefault()).minusWeeks(1);
        var weekBefore = zdt.toInstant();
        var giftCard = GiftCardEntity.builder()
                .initialValue(0d)
                .customers(Set.of(new CustomerGiftCardEntity()))
                .expirationDate(weekBefore)
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.PARTIAL);
    }

    @Test
    @DisplayName("Update gift card status (redeemable) and save success")
    void testUpdateGiftCardStatusRedeemableAndSaveSuccess() {
        // given
        var giftCard = GiftCardEntity.builder()
                .status(GiftCardStatus.REDEEMABLE)
                .build();

        Mockito.when(giftCardRepository.save(Mockito.any(GiftCardEntity.class)))
                .thenReturn(giftCard);

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.REDEEMABLE);
    }

    @Test
    @DisplayName("Update gift card status (redeemable) and return it")
    void testUpdateGiftCardStatusRedeemableAndReturnIt() {
        // given
        var giftCard = GiftCardEntity.builder()
                .status(GiftCardStatus.REDEEMABLE)
                .build();

        // when
        var result = giftCardHelper.updateGiftCardStatusAndSave(giftCard, false);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(GiftCardStatus.REDEEMABLE);
        Assertions.assertThat(result).isEqualTo(giftCard);
    }
}
