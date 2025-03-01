package com.hesmantech.salonbooking.config;

import com.hesmantech.salonbooking.service.schedule.ScheduleUpdateExpiredGiftCardStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
    private final ScheduleUpdateExpiredGiftCardStatus scheduleUpdateExpiredGiftCardStatus;

    @PostConstruct
    public void runSchedules() {
        log.info("Start schedule of update expired gift card status");
        scheduleUpdateExpiredGiftCardStatus.run();
    }
}
