package com.hesmantech.salonbooking.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Table(name = "role")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -2487488969196295389L;

    @Id
    private String id;

    private String name;
}
