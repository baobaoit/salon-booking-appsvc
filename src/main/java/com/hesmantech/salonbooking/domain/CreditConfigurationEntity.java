package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

@Table(name = "credit_configuration")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "conversionCredit", "creditThreshold"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditConfigurationEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = 6897084707966277829L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double conversionCredit;
    private double creditThreshold;
}
