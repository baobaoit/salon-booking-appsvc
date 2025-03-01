package com.hesmantech.salonbooking.api.dto.nailservice;

import com.hesmantech.salonbooking.domain.model.service.ServicePriceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateNailServiceRequest(
        @NotBlank String name,
        @NotNull @Min(0) Double startPrice,
        @Min(0) Double endPrice,
        @NotNull ServicePriceType priceType,
        UUID groupId
) {
}
