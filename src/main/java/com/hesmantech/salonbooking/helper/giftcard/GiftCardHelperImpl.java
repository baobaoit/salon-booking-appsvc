package com.hesmantech.salonbooking.helper.giftcard;

import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardAlreadyRedeemedException;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GiftCardHelperImpl implements GiftCardHelper {
    private final CustomerGiftCardRepository customerGiftCardRepository;
    private final GiftCardRepository giftCardRepository;

    @Override
    public CustomerGiftCardEntity redeemGiftCardForCustomer(CustomerGiftCardEntity customerGiftCard) {
        if (customerGiftCard.isRedeemed()) {
            throw new GiftCardAlreadyRedeemedException(customerGiftCard.getGiftCard().getCode());
        }

        customerGiftCard.setRedeemed(true);
        return customerGiftCardRepository.save(customerGiftCard);
    }

    @Override
    public GiftCardEntity updateGiftCardStatusAndSave(GiftCardEntity giftCard, boolean isUseEntityFromRepository) {
        updateGiftCardStatus(giftCard);
        return isUseEntityFromRepository ? giftCardRepository.save(giftCard) : giftCard;
    }

    private void updateGiftCardStatus(GiftCardEntity giftCard) {
        if (Stream.of(GiftCardStatus.DEACTIVATED, GiftCardStatus.REDEEMABLE)
                .anyMatch(s -> s.equals(giftCard.getStatus()))) {
            return;
        }

        var customers = giftCard.getCustomers();
        if (giftCard.getInitialValue() == 0 || CollectionUtils.isEmpty(customers)) {
            giftCard.setStatus(GiftCardStatus.PARTIAL);
            return;
        } else {
            giftCard.setStatus(GiftCardStatus.FULL);
        }

        Instant expirationDate = giftCard.getExpirationDate();
        Instant today = Instant.now();
        boolean isExpired = expirationDate != null && expirationDate.isBefore(today);
        if (isExpired) {
            giftCard.setStatus(GiftCardStatus.EXPIRED);
        }
    }
}
