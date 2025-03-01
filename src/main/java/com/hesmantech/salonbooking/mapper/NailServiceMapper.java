package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceDetailsResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceResponse;
import com.hesmantech.salonbooking.domain.OrderEntity;
import com.hesmantech.salonbooking.domain.OrderedDetailsEntity;
import com.hesmantech.salonbooking.domain.ServiceEntity;

import java.util.List;

public interface NailServiceMapper {
    NailServiceResponse toNailServiceResponse(ServiceEntity entity);

    NailServiceResponse toNailServiceResponse(OrderedDetailsEntity orderedDetailsEntity);

    List<NailServiceResponse> toNailServiceResponseList(OrderEntity orderEntity);

    NailServiceDetailsResponse toNailServiceDetailsResponse(ServiceEntity entity);
}
