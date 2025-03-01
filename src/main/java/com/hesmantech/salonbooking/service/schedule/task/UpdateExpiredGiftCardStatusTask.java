package com.hesmantech.salonbooking.service.schedule.task;

import com.hesmantech.salonbooking.domain.model.giftcard.GiftCardStatus;
import com.hesmantech.salonbooking.repository.GiftCardRepository;
import com.hesmantech.salonbooking.service.schedule.task.base.ScheduleTaskable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.hesmantech.salonbooking.domain.QGiftCardEntity.giftCardEntity;

@Component
@RequiredArgsConstructor
public class UpdateExpiredGiftCardStatusTask implements ScheduleTaskable {
    private final GiftCardRepository giftCardRepository;

    @Transactional
    @Override
    public void perform() {
        var criteria = giftCardEntity.status.ne(GiftCardStatus.EXPIRED)
                .and(giftCardEntity.expirationDate.before(Instant.now()));

        var gitfCards = giftCardRepository.findAllGiftCards(criteria);
        for (var giftCard : gitfCards) {
            giftCard.setStatus(GiftCardStatus.EXPIRED);
        }
        giftCardRepository.saveAll(gitfCards);
    }
}
