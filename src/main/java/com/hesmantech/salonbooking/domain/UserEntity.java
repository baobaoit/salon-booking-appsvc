package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.user.UserGender;
import com.hesmantech.salonbooking.domain.model.user.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(name = "users")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "firstName", "lastName", "userId", "phoneNumber"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = -4003210456237768978L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;

    //region Login credentials
    private String userId;
    private String phoneNumber;
    private String password;
    //endregion

    @OneToMany(mappedBy = "customer")
    private List<OrderEntity> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private RoleEntity role;

    // Date of birth
    private Instant dob;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "customer")
    private Set<CustomerGiftCardEntity> giftCards = new HashSet<>();

    public void addGiftCard(GiftCardEntity giftCard) {
        CustomerGiftCardEntity customerGiftCard = new CustomerGiftCardEntity(this, giftCard);
        giftCards.add(customerGiftCard);
        giftCard.getCustomers().add(customerGiftCard);
    }
}
