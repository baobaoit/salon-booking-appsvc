package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
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

@Table(name = "credit")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "totalCredit", "availableCredit", "totalNoGiftCard", "availableNoGiftCard",
        "redeemNoGiftCard"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = 7739244796093766822L;

    @Id
    private UUID id;

    private double totalCredit;
    private double availableCredit;
    private int totalNoGiftCard;
    private int availableNoGiftCard;
    private int redeemNoGiftCard;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private UserEntity customer;
}
