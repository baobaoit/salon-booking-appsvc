package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static com.hesmantech.salonbooking.constants.Constants.GIFT_CARD_DOESNT_EXPIRE;

class GiftCardMapperTests {
    private static final GiftCardMapper giftCardMapper = GiftCardMapper.INSTANCE;

    @Test
    void testToGiftCardResponseFailed() {
        // when
        var result = giftCardMapper.toGiftCardResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToGiftCardResponseSuccess() {
        // given
        var giftCardEntity = new GiftCardEntity();
        giftCardEntity.setId(UUID.randomUUID());
        giftCardEntity.setStatus(GiftCardStatus.PARTIAL);

        // when
        var result = giftCardMapper.toGiftCardResponse(giftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(giftCardEntity.getId().toString());
        Assertions.assertThat(result.status()).isEqualTo(giftCardEntity.getStatus().toString());
        Assertions.assertThat(result.hasExpirationDate()).isFalse();
        Assertions.assertThat(result.expirationDate()).isEqualTo(GIFT_CARD_DOESNT_EXPIRE);
    }

    @Test
    void testToGiftCardResponseSomeNullsSuccess() {
        // given
        var giftCardEntity = new GiftCardEntity();

        // when
        var result = giftCardMapper.toGiftCardResponse(giftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
        Assertions.assertThat(result.status()).isNull();
        Assertions.assertThat(result.hasExpirationDate()).isFalse();
        Assertions.assertThat(result.expirationDate()).isEqualTo(GIFT_CARD_DOESNT_EXPIRE);
    }

    @Test
    void testToGiftCardResponseHasExpirationDateSuccess() {
        // given
        var giftCardEntity = new GiftCardEntity();
        giftCardEntity.setId(UUID.randomUUID());
        giftCardEntity.setStatus(GiftCardStatus.PARTIAL);
        giftCardEntity.setExpirationDate(Instant.now());

        // when
        var result = giftCardMapper.toGiftCardResponse(giftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(giftCardEntity.getId().toString());
        Assertions.assertThat(result.status()).isEqualTo(giftCardEntity.getStatus().toString());
        Assertions.assertThat(result.hasExpirationDate()).isTrue();
        Assertions.assertThat(result.expirationDate()).isEqualTo(giftCardEntity.getExpirationDate().toString());
    }

    @Test
    void toSearchGiftCardResponseFailed() {
        // when
        var result = giftCardMapper.toSearchGiftCardResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toSearchGiftCardResponseSuccess() {
        // given
        var giftCardEntity = new GiftCardEntity();
        giftCardEntity.setId(UUID.randomUUID());
        giftCardEntity.setStatus(GiftCardStatus.PARTIAL);
        var customerGiftCardEntity = new CustomerGiftCardEntity();
        var customer = new UserEntity();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Customer first name");
        customer.setLastName("Customer last name");
        customerGiftCardEntity.setCustomer(customer);
        customerGiftCardEntity.setRedeemed(true);
        giftCardEntity.setCustomers(Set.of(customerGiftCardEntity));

        // when
        var result = giftCardMapper.toSearchGiftCardResponse(giftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(giftCardEntity.getId().toString());
        Assertions.assertThat(result.status()).isEqualTo(giftCardEntity.getStatus().toString());
        Assertions.assertThat(result.customerId()).isEqualTo(customer.getId().toString());
        Assertions.assertThat(result.customerName()).isEqualTo(customer.getFirstName() + " " + customer.getLastName());
        Assertions.assertThat(result.isRedeemed()).isTrue();
    }

    @Test
    void toSearchGiftCardResponseCustomerIdIsNullSuccess() {
        // given
        var giftCardEntity = new GiftCardEntity();
        var customerGiftCardEntity = new CustomerGiftCardEntity();
        customerGiftCardEntity.setCustomer(new UserEntity());
        giftCardEntity.setCustomers(Set.of(customerGiftCardEntity));

        // when
        var result = giftCardMapper.toSearchGiftCardResponse(giftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.customerId()).isNull();
    }

    @Test
    void toSearchGiftCardResponseSomeNullsSuccess() {
        // given
        var giftCardEntity = new GiftCardEntity();

        // when
        var result = giftCardMapper.toSearchGiftCardResponse(giftCardEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
        Assertions.assertThat(result.status()).isNull();
    }
}
