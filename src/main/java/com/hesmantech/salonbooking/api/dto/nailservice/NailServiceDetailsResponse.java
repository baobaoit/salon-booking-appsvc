package com.hesmantech.salonbooking.api.dto.nailservice;

import com.hesmantech.salonbooking.api.dto.group.GroupResponse;
import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
import com.hesmantech.salonbooking.domain.model.service.ServiceStatus;
import lombok.With;

public record NailServiceDetailsResponse(
        String id,
        String name,
        @With GroupResponse group,
        ServiceStatus status,
        Double startPrice,
        Double endPrice,
        ServicePriceType priceType,
        String createdDate
) {
}
