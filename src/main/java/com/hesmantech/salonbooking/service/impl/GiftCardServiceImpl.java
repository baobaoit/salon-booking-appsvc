package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.giftcard.CreateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.SearchGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.giftcard.UpdateGiftCardRequest;
import com.hesmantech.salonbooking.api.dto.sort.giftcard.GiftCardSortProperty;
import com.hesmantech.salonbooking.domain.CustomerGiftCardEntity;
import com.hesmantech.salonbooking.domain.CustomerGiftCardId;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardDoesNotBelongToCustomerException;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardIsNotRedeemableException;
import com.hesmantech.salonbooking.exception.giftcard.GiftCardNotFoundException;
import com.hesmantech.salonbooking.exception.user.CustomerNotFoundException;
import com.hesmantech.salonbooking.helper.giftcard.GiftCardHelper;
import com.hesmantech.salonbooking.mapper.base.InstantMapper;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.CustomerGiftCardSearchParamsBuilder;
import com.hesmantech.salonbooking.security.utils.KeyGenerator;
import com.hesmantech.salonbooking.service.GiftCardService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static com.hesmantech.salonbooking.constants.Constants.DEFAULT_SYSTEM_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardServiceImpl implements GiftCardService {
    private final GiftCardRepository giftCardRepository;
    private final UserRepository userRepository;
    private final CustomerGiftCardRepository customerGiftCardRepository;
    private final GiftCardHelper giftCardHelper;

    @Value("${app.gift-code-length}")
    private int giftCodeLength;

    @Override
    public GiftCardEntity create(CreateGiftCardRequest request) {
        var giftCard = giftCardRepository.save(buildGiftCard(request));
        associateCustomersWithGiftCard(request.customers(), giftCard);
        return processGiftCardThenReturnSaved(giftCard);
    }

    @Override
    public GiftCardEntity getDetails(UUID id) {
        return giftCardRepository.findById(id)
                .map(this::processGiftCardThenReturnSaved)
                .orElseThrow(() -> new GiftCardNotFoundException(id));
    }

    @Override
    public GiftCardEntity update(UUID id, UpdateGiftCardRequest request) {
        return giftCardRepository.findById(id)
                .map(giftCard -> {
                    giftCard.setCode(request.giftCode());
                    giftCard.setInitialValue(request.initialValue());
                    giftCard.setExpirationDate(InstantMapper.from(request.expirationDate()));
                    giftCard.setNotes(request.notes());

                    updateGiftCardCustomers(giftCard, request.customers());

                    return giftCard;
                })
                .map(this::processGiftCardThenReturnSaved)
                .orElseThrow(() -> new GiftCardNotFoundException(id));
    }

    @Override
    public String generateGiftCardCode() {
        String code = KeyGenerator.randomString(giftCodeLength);

        while (giftCardRepository.existsByCode(code)) {
            code = KeyGenerator.randomString(giftCodeLength);
        }

        return code;
    }

    @Override
    public Page<GiftCardEntity> search(int page, int size, Sort.Direction direction,
                                       GiftCardSortProperty property, SearchGiftCardRequest request) {
        var searchParamsBuilder = CustomerGiftCardSearchParamsBuilder
                .from(page, size, direction, property, request);
        final BooleanExpression criteria = searchParamsBuilder.getCommonCriteriaValue();
        final Pageable pageable = searchParamsBuilder.getPageable();

        log.info("Search Gift card with criteria: {}", criteria);

        return giftCardRepository.findAllGiftCards(criteria, pageable)
                .map(this::processGiftCardThenReturnIt)
                .map(this::setGiftCardStatusToRedeemable);
    }

    @Override
    public GiftCardEntity deactive(UUID id) {
        return giftCardRepository.findById(id)
                .map(giftCard -> {
                    if (GiftCardStatus.DEACTIVATED.equals(giftCard.getStatus())) {
                        return giftCard;
                    }

                    giftCard.setStatus(GiftCardStatus.DEACTIVATED);

                    return giftCard;
                })
                .map(this::processGiftCardThenReturnSaved)
                .orElseThrow(() -> new GiftCardNotFoundException(id));
    }

    @Override
    public GiftCardEntity redeem(UUID id, UUID customerId) {
        return giftCardRepository.findByIdAndStatus(id, GiftCardStatus.FULL)
                .map(giftCard -> giftCardHelper.updateGiftCardStatusAndSave(giftCard, true))
                .filter(giftCard -> GiftCardStatus.FULL.equals(giftCard.getStatus()))
                .map(giftCard -> userRepository.findByIdAndStatus(customerId, UserStatus.ACTIVE)
                        .map(customer -> customerGiftCardRepository.findById(CustomerGiftCardId.builder()
                                        .customerId(customerId)
                                        .giftCardId(giftCard.getId())
                                        .build())
                                .map(giftCardHelper::redeemGiftCardForCustomer)
                                .map(customerGiftCard -> cloneGiftCardWithSingleCustomer(giftCard, customerGiftCard))
                                .map(this::processGiftCardThenReturnIt)
                                .orElseThrow(() -> new GiftCardDoesNotBelongToCustomerException(giftCard.getCode(), customer.getFirstName() + " " + customer.getLastName())))
                        .orElseThrow(() -> new CustomerNotFoundException(customerId)))
                .orElseThrow(() -> new GiftCardIsNotRedeemableException(id));
    }

    @Override
    public boolean unlink(UUID id, UUID customerId) {
        return customerGiftCardRepository.findById(CustomerGiftCardId.builder()
                        .giftCardId(id)
                        .customerId(customerId)
                        .build())
                .map(customerGiftCard -> {
                    if (customerGiftCard.isRedeemed()) {
                        return false;
                    }

                    customerGiftCardRepository.delete(customerGiftCard);

                    return true;
                })
                .orElse(false);
    }

    private GiftCardEntity cloneGiftCardWithSingleCustomer(GiftCardEntity giftCard, CustomerGiftCardEntity customerGiftCard) {
        var giftCardClone = giftCard.copy();
        giftCardClone.getCustomers().add(customerGiftCard);
        return setGiftCardStatusToRedeemable(giftCardClone);
    }

    private GiftCardEntity setGiftCardStatusToRedeemable(GiftCardEntity giftCard) {
        var customers = giftCard.getCustomers();
        if (CollectionUtils.isNotEmpty(customers)) {
            var customerGiftCard = customers.iterator().next();
            if (GiftCardStatus.FULL.equals(giftCard.getStatus()) && customerGiftCard.isRedeemed()) {
                giftCard.setStatus(GiftCardStatus.REDEEMABLE);
            }
        }
        return giftCard;
    }

    private void updateGiftCardCustomers(GiftCardEntity giftCard, Collection<UUID> customers) {
        var oldCustomers = giftCard.getCustomers();
        var oldCustomersRedeemed = oldCustomers.stream()
                .filter(CustomerGiftCardEntity::isRedeemed)
                .toList();
        var oldCustomersNotRedeemed = oldCustomers.stream()
                .filter(customerGiftCard -> !customerGiftCard.isRedeemed())
                .toList();
        if (CollectionUtils.isNotEmpty(oldCustomers)) {
            customerGiftCardRepository.deleteAll(oldCustomersNotRedeemed);
        }

        giftCard.setCustomers(new HashSet<>(oldCustomersRedeemed));
        customers.forEach(customerId ->
                userRepository.findById(customerId)
                        .ifPresent(customer -> {
                            var customerGiftCardId = CustomerGiftCardId.builder()
                                    .customerId(customerId)
                                    .giftCardId(giftCard.getId())
                                    .build();
                            if (customerGiftCardRepository.findById(customerGiftCardId).isEmpty()) {
                                customer.addGiftCard(giftCard);
                                customerGiftCardRepository.saveAll(customer.getGiftCards());
                                userRepository.save(customer);
                            }
                        }));
    }

    private GiftCardEntity switchUsernameToName(GiftCardEntity giftCard) {
        String username = giftCard.getCreatedBy();

        if (isSystemUser(username)) {
            return giftCard;
        }

        userRepository.findByUsername(username)
                .ifPresent(user -> giftCard.setCreatedBy(formatFullName(user)));

        return giftCard;
    }

    private boolean isSystemUser(String username) {
        return username == null || username.isEmpty() || DEFAULT_SYSTEM_USER.equalsIgnoreCase(username);
    }

    private String formatFullName(UserEntity user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private GiftCardEntity buildGiftCard(CreateGiftCardRequest request) {
        return GiftCardEntity.builder()
                .code(request.giftCode())
                .initialValue(request.initialValue())
                .expirationDate(InstantMapper.from(request.expirationDate()))
                .notes(request.notes())
                .customers(new HashSet<>())
                .build();
    }

    private void associateCustomersWithGiftCard(Collection<UUID> customerIds, GiftCardEntity giftCard) {
        customerIds.stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent).map(Optional::get)
                .forEach(customer -> {
                    customer.addGiftCard(giftCard);
                    customerGiftCardRepository.saveAll(customer.getGiftCards());
                    userRepository.save(customer);
                });
    }

    private GiftCardEntity processGiftCardThenReturnSaved(GiftCardEntity giftCard) {
        return switchUsernameToName(giftCardHelper.updateGiftCardStatusAndSave(giftCard, true));
    }

    private GiftCardEntity processGiftCardThenReturnIt(GiftCardEntity giftCard) {
        return switchUsernameToName(giftCardHelper.updateGiftCardStatusAndSave(giftCard, false));
    }
}
