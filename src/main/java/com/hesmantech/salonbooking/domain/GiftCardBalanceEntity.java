package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.base.Customerable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "gift_card_balance")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "balance"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCardBalanceEntity extends AbstractAuditEntity implements Customerable {
    @Serial
    private static final long serialVersionUID = -2117714595389897687L;

    @Id
    private UUID id;

    private double balance;

    @Transient
    private double previousBalance;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private UserEntity customer;

    @OneToMany(mappedBy = "giftCardBalance", cascade = CascadeType.ALL)
    private List<GiftCardBalanceActivityEntity> activities = new ArrayList<>();

    public void addActivity(GiftCardBalanceActivityEntity activity) {
        activities.add(activity);
        activity.setGiftCardBalance(this);
    }
}
