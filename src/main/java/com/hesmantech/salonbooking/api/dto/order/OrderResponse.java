package com.hesmantech.salonbooking.api.dto.order;

import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceResponse;
import com.hesmantech.salonbooking.api.dto.user.NailTechnicianResponse;
import lombok.Builder;
import lombok.With;

import java.util.List;

@Builder
public record OrderResponse(
        String id,
        String clientName,
        String customerId,
        @With NailTechnicianResponse nailTechnician,
        String clientNotes,
        String creationTime,
        String checkOutTime,
        String status,
        Double totalPrice,
        Double discount,
        String checkOutNotes,
        @With List<NailServiceResponse> services
) {
}
