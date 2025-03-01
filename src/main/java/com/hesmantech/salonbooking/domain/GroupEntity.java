package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

@Table(name = "groups")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = -4292877777612798379L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "group")
    private List<ServiceEntity> services = new ArrayList<>();
}
