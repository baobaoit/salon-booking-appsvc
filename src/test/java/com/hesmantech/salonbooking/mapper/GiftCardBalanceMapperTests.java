package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.giftcardbalance.LatestGCBResponse;
import com.hesmantech.salonbooking.domain.GiftCardBalanceActivityEntity;
import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.model.giftcardbalance.activity.GCBActivityType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InaccessibleObjectException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

class GiftCardBalanceMapperTests {
    private static final Logger log = LoggerFactory.getLogger(GiftCardBalanceMapperTests.class);
    private static final GiftCardBalanceMapper giftCardBalanceMapper = GiftCardBalanceMapper.INSTANCE;

    @Test
    void testToLatestGCBResponseSuccess() {
        // given
        var giftCardBalanceEntity = new GiftCardBalanceEntity();
        giftCardBalanceEntity.setBalance(12.34);

        var expectedResult = new LatestGCBResponse(BigDecimal.valueOf(12.34)
                .setScale(2, RoundingMode.HALF_UP));

        // when
        var result = giftCardBalanceMapper.toLatestGiftCardBalanceResponse(giftCardBalanceEntity);

        // then
        Assertions.assertThat(result)
                .isNotNull()
                .isInstanceOf(LatestGCBResponse.class)
                .isEqualTo(expectedResult);
    }

    @Test
    void testToLatestGCBResponseButGiftCardIsNull() {
        // when
        var result = giftCardBalanceMapper.toLatestGiftCardBalanceResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToSearchGCBActivityResponseOfOrderSuccess() {
        // given
        var giftCardBalanceActivityEntity = new GiftCardBalanceActivityEntity();
        giftCardBalanceActivityEntity.setType(GCBActivityType.PAID_ORDER);
        var orderEntity = new OrderEntity();
        var orderId = UUID.randomUUID();
        orderEntity.setId(orderId);
        giftCardBalanceActivityEntity.setOrder(orderEntity);

        // when
        var result = giftCardBalanceMapper.toGCBActivityResponse(giftCardBalanceActivityEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.orderId()).isEqualTo(orderId.toString());
        Assertions.assertThat(result.description()).contains(orderId.toString());
    }

    @Test
    void testToSearchGCBActivityResponseOfRedeemGiftCardSuccess() {
        // given
        var giftCardBalanceActivityEntity = new GiftCardBalanceActivityEntity();
        giftCardBalanceActivityEntity.setType(GCBActivityType.REDEEM_GIFT_CARD);
        var giftCardEntity = new GiftCardEntity();
        giftCardEntity.setId(UUID.randomUUID());
        giftCardEntity.setCode("TEST");
        giftCardBalanceActivityEntity.setGiftCard(giftCardEntity);

        // when
        var result = giftCardBalanceMapper.toGCBActivityResponse(giftCardBalanceActivityEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.giftCardId()).isEqualTo(giftCardEntity.getId().toString());
        Assertions.assertThat(result.description()).contains(giftCardEntity.getCode());
    }

    @Test
    void testToSearchGCBActivityResponseOfOrderFullSuccess() {
        // given
        var giftCardBalanceActivityEntity = new GiftCardBalanceActivityEntity();
        giftCardBalanceActivityEntity.setType(GCBActivityType.PAID_ORDER);
        var orderEntity = new OrderEntity();
        orderEntity.setId(UUID.randomUUID());
        giftCardBalanceActivityEntity.setOrder(orderEntity);
        giftCardBalanceActivityEntity.setCreatedDate(Instant.now());


        // when
        var result = giftCardBalanceMapper.toGCBActivityResponse(giftCardBalanceActivityEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.orderId()).isEqualTo(orderEntity.getId().toString());
        Assertions.assertThat(result.date()).isEqualTo(giftCardBalanceActivityEntity.getCreatedDate().toString());
    }

    @Test
    void testToSearchGCBActivityResponseFailed() {
        // when
        var result = giftCardBalanceMapper.toGCBActivityResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testToSearchGCBActivityResponseOfOrderIdNull() {
        // given
        var giftCardBalanceActivityEntity = new GiftCardBalanceActivityEntity();
        giftCardBalanceActivityEntity.setType(GCBActivityType.PAID_ORDER);
        var orderEntity = new OrderEntity();
        giftCardBalanceActivityEntity.setOrder(orderEntity);

        // when
        var result = giftCardBalanceMapper.toGCBActivityResponse(giftCardBalanceActivityEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.orderId()).isNull();
    }

    @Test
    void testToSearchGCBActivityResponseOfRedeemGiftCardIdNull() {
        // given
        var giftCardBalanceActivityEntity = new GiftCardBalanceActivityEntity();
        giftCardBalanceActivityEntity.setType(GCBActivityType.REDEEM_GIFT_CARD);
        var giftCardEntity = new GiftCardEntity();
        giftCardBalanceActivityEntity.setGiftCard(giftCardEntity);

        // when
        var result = giftCardBalanceMapper.toGCBActivityResponse(giftCardBalanceActivityEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.giftCardId()).isNull();
    }

    @Test
    void testToSearchGCBActivityImpossibleCase() {
        try {
            // given
            var giftCardBalanceActivityOrderId = giftCardBalanceMapper.getClass()
                    .getDeclaredMethod("giftCardBalanceActivityOrderId", GiftCardBalanceActivityEntity.class);
            giftCardBalanceActivityOrderId.setAccessible(true);

            var giftCardBalanceActivityGiftCardId = giftCardBalanceMapper.getClass()
                    .getDeclaredMethod("giftCardBalanceActivityGiftCardId", GiftCardBalanceActivityEntity.class);
            giftCardBalanceActivityGiftCardId.setAccessible(true);

            // when
            var resultOrder = giftCardBalanceActivityOrderId.invoke(giftCardBalanceMapper, (GiftCardBalanceActivityEntity) null);
            var resultGiftCard = giftCardBalanceActivityGiftCardId.invoke(giftCardBalanceMapper, (GiftCardBalanceActivityEntity) null);

            // then
            Assertions.assertThat(resultOrder).isNull();
            Assertions.assertThat(resultGiftCard).isNull();
        } catch (NoSuchMethodException e) {
            log.error("The private method is not found: {}", e.getMessage(), e);
        } catch (InaccessibleObjectException e) {
            log.error("Access can not be enabled: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception message: {}", e.getMessage(), e);
        }
    }
}
