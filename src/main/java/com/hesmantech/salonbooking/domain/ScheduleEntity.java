package com.hesmantech.salonbooking.domain;

import com.hesmantech.salonbooking.domain.base.AbstractAuditEntity;
import com.hesmantech.salonbooking.domain.model.schedule.ScheduleType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

@Table(name = "schedule")
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntity extends AbstractAuditEntity {
    @Serial
    private static final long serialVersionUID = -2956663536953224090L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    private String cronExpression;
    private String description;
}
