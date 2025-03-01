package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.giftcardbalance.LatestGCBResponse;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityResponse;
import com.hesmantech.salonbooking.domain.GiftCardBalanceActivityEntity;
import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import com.hesmantech.salonbooking.domain.model.giftcardbalance.activity.GCBActivityType;
import com.hesmantech.salonbooking.mapper.base.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GiftCardBalanceMapper extends BigDecimalMapper {
    GiftCardBalanceMapper INSTANCE = Mappers.getMapper(GiftCardBalanceMapper.class);

    @Named("extractActivityDescription")
    static String extractActivityDescription(GiftCardBalanceActivityEntity giftCardBalanceActivity) {
        var activityType = giftCardBalanceActivity.getType();
        var sb = new StringBuilder(activityType.getDescription());
        sb.append(" ");

        if (GCBActivityType.PAID_ORDER.equals(activityType)) {
            sb.append(giftCardBalanceActivity.getOrder().getId());
        } else {
            sb.append("Claim code: ").append(giftCardBalanceActivity.getGiftCard().getCode());
        }

        return sb.toString();
    }

    @Mapping(source = "balance", target = "balance", qualifiedByName = "toBigDecimalScale2")
    LatestGCBResponse toLatestGiftCardBalanceResponse(GiftCardBalanceEntity giftCardBalance);

    @Mapping(source = "createdDate", target = "date")
    @Mapping(source = "amount", target = "amount", qualifiedByName = "toBigDecimalScale2")
    @Mapping(source = "closingBalance", target = "closingBalance", qualifiedByName = "toBigDecimalScale2")
    @Mapping(source = "type", target = "activityType")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "giftCard.id", target = "giftCardId")
    @Mapping(source = "giftCardBalanceActivity", target = "description", qualifiedByName = "extractActivityDescription")
    SearchGCBActivityResponse toGCBActivityResponse(GiftCardBalanceActivityEntity giftCardBalanceActivity);
}
