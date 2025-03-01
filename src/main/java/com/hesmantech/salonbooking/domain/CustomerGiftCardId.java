package com.hesmantech.salonbooking.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode(of = {"customerId", "giftCardId"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerGiftCardId implements Serializable {
    private UUID customerId;
    private UUID giftCardId;
}
