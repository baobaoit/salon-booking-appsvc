package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.giftcardbalance.activity.GCBActivityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

@Table(name = "gift_card_balance_activity")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "type", "amount", "closingBalance"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCardBalanceActivityEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = -3287980015351881517L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GCBActivityType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", referencedColumnName = "id")
    private OrderEntity order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_card_id", referencedColumnName = "id")
    private GiftCardEntity giftCard;

    private double amount;
    private double closingBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_card_balance_id", referencedColumnName = "customer_id")
    private GiftCardBalanceEntity giftCardBalance;
}
