package com.hesmantech.salonbooking.api.impl;

import com.hesmantech.salonbooking.api.GroupResource;
import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.group.CreateGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.GroupResponse;
import com.hesmantech.salonbooking.api.dto.group.SearchGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.UpdateGroupRequest;
import com.hesmantech.salonbooking.api.dto.sort.group.GroupSortProperty;
import com.hesmantech.salonbooking.mapper.GroupMapper;
import com.hesmantech.salonbooking.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupResourceImpl implements GroupResource {
    private static final GroupMapper groupMapper = GroupMapper.INSTANCE;
    private final GroupService groupService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GroupResponse createGroup(CreateGroupRequest groupRequest, Principal principal) {
        try {
            GroupResponse groupResponse = groupMapper.toGroupResponse(groupService.create(groupRequest));

            log.info("Created a new group successfully from {}", principal.getName());

            return groupResponse;
        } catch (Exception e) {
            log.error("Failed to create a new group: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GroupResponse update(@PathVariable UUID id, UpdateGroupRequest groupRequest, Principal principal) {
        try {
            GroupResponse groupResponse = groupMapper.toGroupResponse(
                    groupService.update(id, groupRequest));

            log.info("Update group {} successfully from {}", id, principal.getName());

            return groupResponse;
        } catch (Exception e) {
            log.error("Failed to update group {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Override
    public GroupResponse getDetails(@PathVariable UUID id, Principal principal) {
        try {
            GroupResponse groupResponse = groupMapper.toGroupResponse(
                    groupService.getDetails(id));

            log.info("Get details of group {} successfully from {}", id, principal.getName());

            return groupResponse;
        } catch (Exception e) {
            log.error("Failed to get details of group {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/search")
    @Override
    public PageResponse<GroupResponse> search(int page, int size,
                                              Sort.Direction direction, GroupSortProperty property,
                                              SearchGroupRequest searchGroupRequest, Principal principal) {
        try {
            Page<GroupResponse> pageGroupResponse = groupService.search(page, size, direction, property, searchGroupRequest)
                    .map(groupMapper::toGroupResponse);

            log.info("Search group service successfully from {}", principal.getName());

            return PageResponse.<GroupResponse>builder()
                    .content(pageGroupResponse.getContent())
                    .page(pageGroupResponse.getPageable().getPageNumber())
                    .size(pageGroupResponse.getSize())
                    .direction(direction.name())
                    .property(property.getProperty())
                    .totalPages(pageGroupResponse.getTotalPages())
                    .totalElements(pageGroupResponse.getTotalElements())
                    .build();
        } catch (Exception e) {
            log.error("Failed to search group service: {}", e.getMessage(), e);
            throw e;
        }
    }
}
