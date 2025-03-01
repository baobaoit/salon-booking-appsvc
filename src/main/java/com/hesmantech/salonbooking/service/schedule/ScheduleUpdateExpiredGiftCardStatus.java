package com.hesmantech.salonbooking.service.schedule;

import com.hesmantech.salonbooking.domain.model.schedule.ScheduleType;
import com.hesmantech.salonbooking.repository.ScheduleRepository;
import com.hesmantech.salonbooking.service.schedule.task.UpdateExpiredGiftCardStatusTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleUpdateExpiredGiftCardStatus implements Runnable {
    private final TaskScheduler taskScheduler;
    private final ScheduleRepository scheduleRepository;
    private final UpdateExpiredGiftCardStatusTask scheduleTask;

    @Override
    public void run() {
        scheduleRepository.findByScheduleType(ScheduleType.UPDATE_EXPIRED_GIFT_CARD_STATUS)
                .ifPresent(scheduleEntity -> {
                    var cronExpression = scheduleEntity.getCronExpression();
                    log.info("Executting Cron job {} for update expired gift card status", cronExpression);

                    taskScheduler.schedule(scheduleTask::perform, new CronTrigger(cronExpression));
                });
    }
}
