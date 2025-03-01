package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.customercredit.SearchCustomerCreditRequest;
import com.hesmantech.salonbooking.api.dto.sort.customercredit.CustomerCreditSortProperty;
import com.hesmantech.salonbooking.domain.CreditConfigurationEntity;
import com.hesmantech.salonbooking.domain.CreditEntity;
import com.hesmantech.salonbooking.domain.GiftCardBalanceEntity;
import com.hesmantech.salonbooking.domain.GiftCardEntity;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.UserEntity;
import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.base.Customerable;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import com.hesmantech.salonbooking.exception.credit.CreditNotFoundException;
import com.hesmantech.salonbooking.exception.creditconfiguration.CreditConfigurationNotFoundException;
import com.hesmantech.salonbooking.repository.CreditConfigurationRepository;
import com.hesmantech.salonbooking.repository.CreditRepository;
import com.hesmantech.salonbooking.repository.CustomerGiftCardRepository;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import com.hesmantech.salonbooking.repository.OrderRepository;
import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.CustomerCreditSearchParamsBuilder;
import com.hesmantech.salonbooking.service.CreditService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {
    private static final Set<String> SUPPORTED_ENTITIES = Set.of(
            OrderEntity.class.getSimpleName(),
            GiftCardEntity.class.getSimpleName(),
            GiftCardBalanceEntity.class.getSimpleName()
    );

    private final CreditRepository creditRepository;
    private final CreditConfigurationRepository creditConfigurationRepository;
    private final OrderRepository orderRepository;
    private final CustomerGiftCardRepository customerGiftCardRepository;
    private final GiftCardRepository giftCardRepository;
    private final UserRepository userRepository;

    @Override
    public <T extends AbstractAuditEntity> void updateBasedOn(T entity) {
        if (!SUPPORTED_ENTITIES.contains(entity.getClass().getSimpleName())) {
            return;
        }

        var creditConfiguration = creditConfigurationRepository.findTopByOrderByCreatedDateDesc()
                .orElseThrow(CreditConfigurationNotFoundException::new);

        UserEntity customer = extractCustomer(entity);

        if (customer == null) {
            return;
        }

        var customerId = customer.getId();

        CreditEntity creditEntity = creditRepository.findById(customerId)
                .orElseGet(CreditEntity::new);

        if (creditEntity.getCustomer() == null) {
            creditEntity.setCustomer(customer);
        }

        updateCreditDetails(creditEntity, customerId, creditConfiguration);

        creditRepository.save(creditEntity);
    }

    @Override
    public Page<CreditEntity> search(int page, int size, Sort.Direction direction, CustomerCreditSortProperty property,
                                     SearchCustomerCreditRequest request) {
        var searchParamsBuilder = CustomerCreditSearchParamsBuilder.from(page, size, direction, property, request);
        final Optional<BooleanExpression> criteria = searchParamsBuilder.getCommonCriteria();
        final Pageable pageable = searchParamsBuilder.getPageable();

        log.info("Search Customer credit with criteria: {}", criteria);

        var creditConfiguration = creditConfigurationRepository.findTopByOrderByCreatedDateDesc()
                .orElseThrow(CreditConfigurationNotFoundException::new);

        return criteria.map(c -> creditRepository.findAllCredits(c, pageable))
                .orElseGet(() -> creditRepository.findAllCredits(pageable))
                .map(creditEntity -> {
                    var customerId = creditEntity.getId();

                    updateCreditDetails(creditEntity, customerId, creditConfiguration);

                    return creditRepository.save(creditEntity);
                });
    }

    @Override
    public CreditEntity getDetails(UUID customerId) {
        return creditRepository.findById(customerId)
                .map(creditEntity -> creditConfigurationRepository.findTopByOrderByCreatedDateDesc()
                        .map(creditConfiguration -> {
                            updateCreditDetails(creditEntity, customerId, creditConfiguration);

                            return creditRepository.save(creditEntity);
                        })
                        .map(this::getAuditorDetails)
                        .orElseThrow(CreditConfigurationNotFoundException::new))
                .orElseThrow(() -> new CreditNotFoundException(customerId));
    }

    private UserEntity extractCustomer(AbstractAuditEntity entity) {
        Customerable customer = null;
        if (entity instanceof GiftCardEntity giftCardEntity) {
            var customers = giftCardEntity.getCustomers();
            if (CollectionUtils.isNotEmpty(customers) && customers.size() == 1) {
                customer = giftCardEntity.getCustomers().iterator().next();
            }
        } else {
            customer = (Customerable) entity;
        }
        return customer == null ? null : customer.getCustomer();
    }

    private void updateCreditDetails(CreditEntity creditEntity, UUID customerId, CreditConfigurationEntity creditConfiguration) {
        double customerTotalPrice = orderRepository
                .totalPriceByCustomerIdAndCustomerStatusIn(customerId, List.of(UserStatus.ACTIVE));
        double totalCredit = customerTotalPrice / creditConfiguration.getConversionCredit();
        creditEntity.setTotalCredit(totalCredit);

        int redeemNoGiftCard = customerGiftCardRepository.countByCustomerIdAndRedeemedIsTrue(customerId);
        double availableCredit = totalCredit - (creditConfiguration.getCreditThreshold() * redeemNoGiftCard);
        creditEntity.setAvailableCredit(availableCredit);
        creditEntity.setRedeemNoGiftCard(redeemNoGiftCard);

        int availableNoGiftCard = giftCardRepository.countAvailableNoGiftCard(customerId);
        creditEntity.setAvailableNoGiftCard(availableNoGiftCard);

        int totalNoGiftCard = giftCardRepository.countTotalNoGiftCard(customerId);
        creditEntity.setTotalNoGiftCard(totalNoGiftCard);
    }

    private CreditEntity getAuditorDetails(CreditEntity creditEntity) {
        String createdBy = creditEntity.getCreatedBy();
        userRepository.findByUsername(createdBy)
                .map(this::extractAuditorName)
                .ifPresent(creditEntity::setCreatedBy);

        String lastModifiedBy = creditEntity.getLastModifiedBy();
        userRepository.findByUsername(lastModifiedBy)
                .map(this::extractAuditorName)
                .ifPresent(creditEntity::setLastModifiedBy);

        return creditEntity;
    }

    private String extractAuditorName(UserEntity user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
