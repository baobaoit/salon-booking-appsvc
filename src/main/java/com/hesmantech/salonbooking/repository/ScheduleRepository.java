package com.hesmantech.salonbooking.repository;

import com.hesmantech.salonbooking.domain.ScheduleEntity;
import com.hesmantech.salonbooking.domain.model.schedule.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, UUID> {
    Optional<ScheduleEntity> findByScheduleType(ScheduleType scheduleType);
}
