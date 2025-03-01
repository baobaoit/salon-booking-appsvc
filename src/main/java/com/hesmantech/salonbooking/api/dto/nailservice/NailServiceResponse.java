package com.hesmantech.salonbooking.api.dto.nailservice;

public record NailServiceResponse(
        String id,
        String name,
        String price,
        String group,
        String createdDate
) {
}
