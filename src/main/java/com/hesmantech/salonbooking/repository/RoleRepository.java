package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {
}
