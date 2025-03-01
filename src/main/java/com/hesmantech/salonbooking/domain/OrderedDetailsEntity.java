package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
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

@Table(name = "ordered_details")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderedDetailsEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = -5923270680002099989L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id", referencedColumnName = "id")
    private OrderEntity order;

    private UUID employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeePhoneNumber;

    private UUID serviceId;
    private String serviceName;
    private Double serviceStartPrice;
    private Double serviceEndPrice;
    private String serviceGroupName;

    @Enumerated(EnumType.STRING)
    private ServicePriceType servicePriceType;
}
