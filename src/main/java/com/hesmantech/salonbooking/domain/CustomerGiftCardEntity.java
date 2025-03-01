package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.base.Customerable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

@Table(name = "customer_gift_card")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"customer", "giftCard"}, callSuper = false)
@NoArgsConstructor
public class CustomerGiftCardEntity extends AbstractAuditEntity implements Customerable {
    @Serial
    private static final long serialVersionUID = -5397395312907490822L;

    @EmbeddedId
    private CustomerGiftCardId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    private UserEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("giftCardId")
    private GiftCardEntity giftCard;

    private boolean redeemed;

    @Builder
    public CustomerGiftCardEntity(UserEntity customer, GiftCardEntity giftCard) {
        this.customer = customer;
        this.giftCard = giftCard;
        this.id = new CustomerGiftCardId(customer.getId(), giftCard.getId());
    }
}
