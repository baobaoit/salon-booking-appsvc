package com.hesmantech.salonbooking.service.impl;

import com.hesmantech.salonbooking.api.dto.group.CreateGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.SearchGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.UpdateGroupRequest;
import com.hesmantech.salonbooking.api.dto.sort.group.GroupSortProperty;
import com.hesmantech.salonbooking.domain.GroupEntity;
import com.hesmantech.salonbooking.exception.group.GroupNotFoundException;
import com.hesmantech.salonbooking.repository.GroupRepository;
import com.hesmantech.salonbooking.repository.searchparamsbuilder.GroupSearchParamsBuilder;
import com.hesmantech.salonbooking.service.GroupService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;

    @Override
    public GroupEntity create(CreateGroupRequest groupRequest) {
        return groupRepository.save(GroupEntity.builder()
                .name(groupRequest.name())
                .build());
    }

    @Override
    public GroupEntity update(UUID id, UpdateGroupRequest groupRequest) {
        return groupRepository.findById(id)
                .map(group -> {
                    group.setName(groupRequest.name());

                    return groupRepository.save(group);
                })
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    @Override
    public GroupEntity getDetails(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    @Override
    public Page<GroupEntity> search(int page, int size, Sort.Direction sortDirection, GroupSortProperty sortProperty,
                                    SearchGroupRequest searchGroupRequest) {
        final GroupSearchParamsBuilder searchParamsBuilder = GroupSearchParamsBuilder
                .from(page, size, sortDirection, sortProperty, searchGroupRequest);
        final Optional<BooleanExpression> criteria = searchParamsBuilder.getCommonCriteria();
        final Pageable pageable = searchParamsBuilder.getPageable();

        log.info("Search Group service with criteria: {}", criteria);

        return criteria.map(c -> groupRepository.findAll(c, pageable))
                .orElseGet(() -> groupRepository.findAll(pageable));
    }
}
