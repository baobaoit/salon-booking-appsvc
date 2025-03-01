package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.giftcard.GiftCardResponse;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardResponse;
import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

import static com.hesmantech.salonbooking.constants.Constants.GIFT_CARD_DOESNT_EXPIRE;

@Mapper
public interface GiftCardMapper {
    GiftCardMapper INSTANCE = Mappers.getMapper(GiftCardMapper.class);

    @Named("hasExpirationDate")
    static boolean hasExpirationDate(Instant expirationDate) {
        return expirationDate != null;
    }

    @Named("expirationDateToString")
    static String convertExpirationDateToString(Instant expirationDate) {
        return hasExpirationDate(expirationDate) ? String.valueOf(expirationDate) : GIFT_CARD_DOESNT_EXPIRE;
    }

    @Named("createdDateToString")
    static String createdDateToString(Instant createdDate) {
        return String.valueOf(createdDate);
    }

    @Named("extractCustomerId")
    static String extractCustomerId(Set<CustomerGiftCardEntity> customers) {
        var customer = getCustomer(customers);
        if (customer == null) {
            return null;
        }

        var customerId = customer.getId();
        if (customerId == null) {
            return null;
        }

        return String.valueOf(customerId);
    }

    @Named("extractCustomerName")
    static String extractCustomerName(Set<CustomerGiftCardEntity> customers) {
        var customer = getCustomer(customers);
        if (customer == null) {
            return null;
        }

        return customer.getFirstName() + " " + customer.getLastName();
    }

    @Named("extractIsRedeemed")
    static boolean extractIsRedeemed(Set<CustomerGiftCardEntity> customers) {
        var customerGiftCard = getCustomerGiftCard(customers);
        if (customerGiftCard == null) {
            return false;
        }

        return customerGiftCard.isRedeemed();
    }

    static CustomerGiftCardEntity getCustomerGiftCard(Collection<CustomerGiftCardEntity> customers) {
        if (CollectionUtils.isEmpty(customers)) {
            return null;
        }

        return customers.iterator().next();
    }

    static UserEntity getCustomer(Collection<CustomerGiftCardEntity> customers) {
        var customerGiftCard = getCustomerGiftCard(customers);
        if (customerGiftCard == null) {
            return null;
        }

        return customerGiftCard.getCustomer();
    }

    @Mapping(source = "code", target = "giftCode")
    @Mapping(source = "expirationDate", target = "hasExpirationDate", qualifiedByName = "hasExpirationDate")
    @Mapping(source = "expirationDate", target = "expirationDate", qualifiedByName = "expirationDateToString")
    @Mapping(source = "initialValue", target = "initialBalance")
    @Mapping(target = "customers", ignore = true)
    @Mapping(target = "withCustomers", ignore = true)
    GiftCardResponse toGiftCardResponse(GiftCardEntity giftCardEntity);

    @Mapping(source = "code", target = "giftCode")
    @Mapping(source = "createdDate", target = "dateIssued", qualifiedByName = "createdDateToString")
    @Mapping(source = "customers", target = "customerId", qualifiedByName = "extractCustomerId")
    @Mapping(source = "customers", target = "customerName", qualifiedByName = "extractCustomerName")
    @Mapping(source = "customers", target = "isRedeemed", qualifiedByName = "extractIsRedeemed")
    SearchGiftCardResponse toSearchGiftCardResponse(GiftCardEntity giftCardEntity);
}
