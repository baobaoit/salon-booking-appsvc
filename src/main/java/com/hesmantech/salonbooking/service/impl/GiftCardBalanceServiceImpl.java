package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.giftcardbalance.RedeemByCodeRequest;
import com.hesmantech.salonbooking.api.dto.giftcardbalance.SearchGCBActivityRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcardbalance.GCBActivitySortProperty;
import com.hesmantech.salonbooking.domain.CustomerGiftCardId;
import com.hesmantech.salonbooking.domain.GiftCardBalanceActivityEntity;
import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.base.GiftCardBalanceAdjustAmount;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.domain.model.giftcardbalance.activity.GCBActivityType;
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
import com.hesmantech.salonbooking.repository.searchparamsbuilder.GCBActivitySearchParamsBuilder;
import com.hesmantech.salonbooking.service.GiftCardBalanceService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.hesmantech.salonbooking.constants.Constants.DOUBLE_THRESHOLD;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardBalanceServiceImpl implements GiftCardBalanceService {
    private final GiftCardBalanceRepository giftCardBalanceRepository;
    private final UserRepository userRepository;
    private final GiftCardRepository giftCardRepository;
    private final CustomerGiftCardRepository customerGiftCardRepository;
    private final GiftCardBalanceActivityRepository giftCardBalanceActivityRepository;
    private final GiftCardHelper giftCardHelper;

    @Override
    public GiftCardBalanceEntity getLatestGiftCardBalance(UUID customerId) {
        return userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE)
                .map(ignored -> giftCardBalanceRepository.findById(customerId)
                        .orElseGet(GiftCardBalanceEntity::new))
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    @Override
    public GiftCardBalanceEntity redeemGiftCardCode(UUID customerId, RedeemByCodeRequest request) {
        var giftCode = request.giftCode();

        return giftCardRepository.findByCodeAndStatus(giftCode, GiftCardStatus.FULL)
                .map(giftCard -> giftCardHelper.updateGiftCardStatusAndSave(giftCard, true))
                .filter(giftCard -> GiftCardStatus.FULL.equals(giftCard.getStatus()))
                .map(giftCard -> userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE)
                        .map(customer -> customerGiftCardRepository.findById(CustomerGiftCardId.builder()
                                        .customerId(customerId)
                                        .giftCardId(giftCard.getId())
                                        .build())
                                .map(giftCardHelper::redeemGiftCardForCustomer)
                                .map(ignored -> updateCustomerGiftCardBalance(customer, giftCard))
                                .orElseThrow(() -> new GiftCardDoesNotBelongToCustomerException(giftCode, customer.getFirstName() + " " + customer.getLastName())))
                        .orElseThrow(() -> new CustomerNotFoundException(customerId)))
                .orElseThrow(() -> new GiftCardIsNotRedeemableException(giftCode));
    }

    @Override
    public void updateWhenRedeemGiftCard(GiftCardEntity giftCard) {
        var customers = giftCard.getCustomers();
        if (CollectionUtils.isEmpty(customers) || customers.size() != 1) {
            return;
        }

        var customer = customers.iterator().next().getCustomer();
        updateCustomerGiftCardBalance(customer, giftCard);
    }

    @Override
    public void updateWhenCheckOutOrder(OrderEntity order) {
        var customer = order.getCustomer();

        updateCustomerGiftCardBalance(customer, order);
    }

    @Override
    public Page<GiftCardBalanceActivityEntity> search(int page, int size, Sort.Direction direction,
                                                      GCBActivitySortProperty property, SearchGCBActivityRequest request) {
        var searchParamsBuilder = GCBActivitySearchParamsBuilder.from(page, size, direction, property, request);
        final Optional<BooleanExpression> criteria = searchParamsBuilder.getCommonCriteria();
        final Pageable pageable = searchParamsBuilder.getPageable();

        log.info("Search Gift card balance activity with criteria: {}", criteria);

        return criteria.map(c -> giftCardBalanceActivityRepository.findAll(c, pageable))
                .orElseGet(() -> giftCardBalanceActivityRepository.findAll(pageable));
    }

    public GiftCardBalanceEntity updateCustomerGiftCardBalance(UserEntity customer, GiftCardBalanceAdjustAmount adjustAmount) {
        var giftCardBalance = findGiftCardBalanceAndUpdateBalance(customer, adjustAmount);

        return addActivityForGiftCardBalance(giftCardBalance, adjustAmount);
    }

    private GiftCardBalanceEntity findGiftCardBalanceAndUpdateBalance(UserEntity customer, GiftCardBalanceAdjustAmount adjustAmount) {
        var customerId = customer.getId();

        var giftCardBalance = giftCardBalanceRepository.findById(customerId)
                .orElseGet(GiftCardBalanceEntity::new);

        if (giftCardBalance.getCustomer() == null) {
            giftCardBalance.setCustomer(customer);
        }

        var oldBalance = giftCardBalance.getBalance();
        double newBalance = calculateNewGiftCardBalance(adjustAmount, oldBalance);
        giftCardBalance.setBalance(newBalance);
        giftCardBalance.setPreviousBalance(oldBalance);

        return giftCardBalanceRepository.save(giftCardBalance);
    }

    private double calculateNewGiftCardBalance(GiftCardBalanceAdjustAmount adjustAmount, double oldBalance) {
        double newBalance = oldBalance;
        if (adjustAmount instanceof GiftCardEntity giftCard) {
            newBalance += giftCard.getAmount();
        } else {
            var order = (OrderEntity) adjustAmount;
            var orderSubtotal = order.getAmount();
            var orderDiscount = order.getDiscount();
            var orderTotalPrice = order.getPrice();
            if (!isOrderTotalValid(oldBalance, orderSubtotal, orderDiscount, orderTotalPrice)) {
                throw new OrderTotalPriceInvalidException(orderSubtotal, orderDiscount, oldBalance);
            }

            newBalance = Math.max(0, newBalance - order.getAmount());
        }
        return newBalance;
    }

    private boolean isOrderTotalValid(double oldBalance, double orderSubtotal, double orderDiscount, double orderTotalPrice) {
        if (Double.compare(oldBalance, 0) == 0) {
            throw new GiftCardBalanceIsNotEnoughToPayException();
        }

        var orderSubtotalAfterDiscounted = orderSubtotal - orderDiscount;
        return oldBalance >= orderSubtotalAfterDiscounted ?
                Double.compare(Math.abs(orderTotalPrice), DOUBLE_THRESHOLD) < 0 :
                Double.compare(Math.abs((orderSubtotalAfterDiscounted - oldBalance) - orderTotalPrice), DOUBLE_THRESHOLD) < 0;
    }

    private GiftCardBalanceEntity addActivityForGiftCardBalance(GiftCardBalanceEntity giftCardBalance, GiftCardBalanceAdjustAmount adjustAmount) {
        var giftCardBalanceActivity = GiftCardBalanceActivityEntity.builder()
                .closingBalance(giftCardBalance.getBalance())
                .giftCardBalance(giftCardBalance)
                .build();

        if (adjustAmount instanceof GiftCardEntity giftCard) {
            giftCardBalanceActivity.setType(GCBActivityType.REDEEM_GIFT_CARD);
            giftCardBalanceActivity.setAmount(giftCard.getAmount());
            giftCard.setGiftCardBalanceActivity(giftCardBalanceActivity);
        } else {
            var order = (OrderEntity) adjustAmount;
            giftCardBalanceActivity.setType(GCBActivityType.PAID_ORDER);
            giftCardBalanceActivity.setOrder(order);
            giftCardBalanceActivity.setAmount(Double.compare(giftCardBalance.getBalance(), 0d) == 0 ?
                    giftCardBalance.getPreviousBalance() : order.getAmount());
            order.setGiftCardBalanceActivity(giftCardBalanceActivity);
        }

        giftCardBalance.addActivity(giftCardBalanceActivity);
        giftCardBalanceActivityRepository.save(giftCardBalanceActivity);

        return giftCardBalance;
    }
}
