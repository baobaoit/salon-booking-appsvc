package com.hesmantech.salonbooking.helper.giftcard;

import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;

public interface GiftCardHelper {
    GiftCardEntity updateGiftCardStatusAndSave(GiftCardEntity giftCard, boolean isUseEntityFromRepository);

    CustomerGiftCardEntity redeemGiftCardForCustomer(CustomerGiftCardEntity customerGiftCard);
}
