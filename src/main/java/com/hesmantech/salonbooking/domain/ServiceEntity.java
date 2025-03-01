package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
import com.hesmantech.salonbooking.domain.model.service.ServiceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

@Table(name = "service")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = -3142327428123151598L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private Double startPrice;
    private Double endPrice;

    @Enumerated(EnumType.STRING)
    private ServicePriceType servicePriceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups_id", referencedColumnName = "id")
    private GroupEntity group;

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;
}
