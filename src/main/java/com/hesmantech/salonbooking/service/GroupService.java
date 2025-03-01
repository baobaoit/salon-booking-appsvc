package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.group.CreateGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.SearchGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.UpdateGroupRequest;
import com.hesmantech.salonbooking.api.dto.sort.group.GroupSortProperty;
import com.hesmantech.salonbooking.domain.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public interface GroupService {
    GroupEntity create(CreateGroupRequest groupRequest);

    GroupEntity update(UUID id, UpdateGroupRequest groupRequest);

    GroupEntity getDetails(UUID id);

    Page<GroupEntity> search(int page, int size, Sort.Direction sortDirection, GroupSortProperty sortProperty, SearchGroupRequest searchGroupRequest);
}
