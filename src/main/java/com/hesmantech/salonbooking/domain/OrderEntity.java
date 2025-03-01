package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.base.Customerable;
import com.hesmantech.salonbooking.domain.base.GiftCardBalanceAdjustAmount;
import com.hesmantech.salonbooking.domain.model.order.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "orders")
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends AbstractAuditEntity implements Customerable, GiftCardBalanceAdjustAmount {
    @Serial
    private static final long serialVersionUID = 3599961505881878330L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String customerNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private UserEntity customer;

    @OneToMany(mappedBy = "order")
    private List<OrderDetailsEntity> orderDetails = new ArrayList<>();

    private Double price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String checkOutNotes;
    private Double discount;

    @OneToMany(mappedBy = "order")
    private List<OrderedDetailsEntity> orderedDetails = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private UserEntity employee;

    private Instant checkOutTime;

    @Transient
    private Double subtotal;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GiftCardBalanceActivityEntity giftCardBalanceActivity;

    public void addOrderDetails(OrderDetailsEntity orderDetails) {
        this.orderDetails.add(orderDetails);
        orderDetails.setOrder(this);
    }

    public void removeOrderDetails(OrderDetailsEntity orderDetails) {
        this.orderDetails.remove(orderDetails);
        orderDetails.setOrder(null);
    }

    public void addOrderedDetails(OrderedDetailsEntity orderedDetails) {
        this.orderedDetails.add(orderedDetails);
        orderedDetails.setOrder(this);
    }

    @Override
    public double getAmount() {
        return subtotal;
    }

    public void setGiftCardBalanceActivity(GiftCardBalanceActivityEntity giftCardBalanceActivity) {
        if (giftCardBalanceActivity == null) {
            if (this.giftCardBalanceActivity != null) {
                this.giftCardBalanceActivity.setOrder(null);
            }
        } else {
            giftCardBalanceActivity.setOrder(this);
        }
        this.giftCardBalanceActivity = giftCardBalanceActivity;
    }
}
