package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.NailServiceResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.CreateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceDetailsResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceResponse;
import com.hesmantech.salonbooking.api.dto.nailservice.NailServiceSearchRequest;
import com.hesmantech.salonbooking.api.dto.nailservice.UpdateNailServiceRequest;
import com.hesmantech.salonbooking.api.dto.sort.service.ServiceSortProperty;
import com.hesmantech.salonbooking.domain.ServiceEntity;
import com.hesmantech.salonbooking.mapper.GroupMapper;
import com.hesmantech.salonbooking.mapper.NailServiceMapper;
import com.hesmantech.salonbooking.service.NailServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nail-services")
@RequiredArgsConstructor
@Slf4j
public class NailServiceResourceImpl implements NailServiceResource {
    private static final GroupMapper groupMapper = GroupMapper.INSTANCE;
    private final NailServiceService nailServiceService;
    private final NailServiceMapper nailServiceMapper;

    @PostMapping("/search")
    @Override
    public PageResponse<NailServiceResponse> search(int page, int size,
                                                    Sort.Direction direction, ServiceSortProperty property,
                                                    NailServiceSearchRequest searchRequest, Principal principal) {
        try {
            Page<NailServiceResponse> pageNailServiceResponse = nailServiceService.search(page, size, direction, property, searchRequest)
                    .map(nailServiceMapper::toNailServiceResponse);

            log.info("Search nail service successfully from {}", principal.getName());

            return PageResponse.<NailServiceResponse>builder()
                    .content(pageNailServiceResponse.getContent())
                    .page(pageNailServiceResponse.getPageable().getPageNumber())
                    .size(pageNailServiceResponse.getSize())
                    .totalPages(pageNailServiceResponse.getTotalPages())
                    .totalElements(pageNailServiceResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search nail service: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public NailServiceResponse create(CreateNailServiceRequest createNailServiceRequest, Principal principal) {
        try {
            NailServiceResponse nailServiceResponse = nailServiceMapper.toNailServiceResponse(
                    nailServiceService.create(createNailServiceRequest));

            log.info("Create nail service successfully from {}", principal.getName());

            return nailServiceResponse;
        } catch (Exception e) {
            log.error("Failed to create nail service: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public NailServiceDetailsResponse update(@PathVariable UUID id, UpdateNailServiceRequest updateNailServiceRequest, Principal principal) {
        try {
            ServiceEntity serviceEntity = nailServiceService.update(id, updateNailServiceRequest);
            NailServiceDetailsResponse nailServiceResponse = nailServiceMapper.toNailServiceDetailsResponse(
                            serviceEntity)
                    .withGroup(groupMapper.toGroupResponse(serviceEntity.getGroup()));

            log.info("Update nail service successfully from {}", principal.getName());

            return nailServiceResponse;
        } catch (Exception e) {
            log.error("Failed to update nail service: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public NailServiceDetailsResponse getDetails(@PathVariable UUID id, Principal principal) {
        try {
            ServiceEntity serviceEntity = nailServiceService.getDetails(id);
            NailServiceDetailsResponse nailServiceResponse = nailServiceMapper.toNailServiceDetailsResponse(
                            serviceEntity)
                    .withGroup(groupMapper.toGroupResponse(serviceEntity.getGroup()));

            log.info("Get nail service details from {}", principal.getName());

            return nailServiceResponse;
        } catch (Exception e) {
            log.error("Failed to get nail service {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public NailServiceDetailsResponse delete(@PathVariable UUID id, Principal principal) {
        try {
            ServiceEntity serviceEntity = nailServiceService.delete(id);
            NailServiceDetailsResponse nailServiceResponse = nailServiceMapper.toNailServiceDetailsResponse(
                            serviceEntity)
                    .withGroup(groupMapper.toGroupResponse(serviceEntity.getGroup()));

            log.info("Delete nail service from {}", principal.getName());

            return nailServiceResponse;
        } catch (Exception e) {
            log.error("Failed to delete nail service: {}", e.getMessage(), e);
            throw e;
        }
    }
}
