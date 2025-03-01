package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.base.GiftCardBalanceAdjustAmount;
import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import java.io.Serial;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "gift_card")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "code"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GiftCardEntity extends AbstractAuditEntity implements GiftCardBalanceAdjustAmount {
    @Serial
    private static final long serialVersionUID = -6573055432222578414L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NaturalId
    private String code;

    private double initialValue;

    private Instant expirationDate;

    private String notes;

    @OneToMany(mappedBy = "giftCard")
    private Set<CustomerGiftCardEntity> customers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private GiftCardStatus status;

    @OneToOne(mappedBy = "giftCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GiftCardBalanceActivityEntity giftCardBalanceActivity;

    public GiftCardEntity copy() {
        var giftCard = GiftCardEntity.builder()
                .id(id)
                .code(code)
                .initialValue(initialValue)
                .expirationDate(expirationDate)
                .notes(notes)
                .status(status)
                .build();

        giftCard.setCreatedBy(this.getCreatedBy());
        giftCard.setCreatedDate(this.getCreatedDate());
        giftCard.customers = new HashSet<>();

        return giftCard;
    }

    @Override
    public double getAmount() {
        return initialValue;
    }

    public void setGiftCardBalanceActivity(GiftCardBalanceActivityEntity giftCardBalanceActivity) {
        if (giftCardBalanceActivity == null) {
            if (this.giftCardBalanceActivity != null) {
                this.giftCardBalanceActivity.setGiftCard(null);
            }
        } else {
            giftCardBalanceActivity.setGiftCard(this);
        }
        this.giftCardBalanceActivity = giftCardBalanceActivity;
    }
}
