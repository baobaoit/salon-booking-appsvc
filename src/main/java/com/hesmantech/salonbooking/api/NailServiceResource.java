package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.CreateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceDetailsResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceSearchRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.UpdateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.sort.service.ServiceSortProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Nail Service Management")
@Validated
public interface NailServiceResource {
    PageResponse<NailServiceResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                             @RequestParam(required = false, defaultValue = "100") @Min(1) int size,
                                             @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                             @RequestParam(required = false, defaultValue = "GROUP_NAME") ServiceSortProperty property,
                                             @RequestBody NailServiceSearchRequest searchRequest,
                                             Principal principal);

    NailServiceResponse create(@Valid @RequestBody CreateNailServiceRequest createNailServiceRequest, Principal principal);

    NailServiceDetailsResponse update(UUID id, @Valid @RequestBody UpdateNailServiceRequest updateNailServiceRequest, Principal principal);

    NailServiceDetailsResponse getDetails(UUID id, Principal principal);

    NailServiceDetailsResponse delete(UUID id, Principal principal);
}
