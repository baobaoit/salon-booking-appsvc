package com.hesmantech.salonbooking.api;

import com.hesmantech.salonbooking.api.dto.PageResponse;
import com.hesmantech.salonbooking.api.dto.group.CreateGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.GroupResponse;
import com.hesmantech.salonbooking.api.dto.group.SearchGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.UpdateGroupRequest;
import com.hesmantech.salonbooking.api.dto.sort.group.GroupSortProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Group Resource")
@Validated
public interface GroupResource {
    GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest groupRequest, Principal principal);

    GroupResponse update(UUID id, @Valid @RequestBody UpdateGroupRequest groupRequest, Principal principal);

    GroupResponse getDetails(UUID id, Principal principal);

    PageResponse<GroupResponse> search(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                       @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
                                       @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                       @RequestParam(required = false, defaultValue = "CREATED_DATE") GroupSortProperty property,
                                       @RequestBody SearchGroupRequest searchGroupRequest, Principal principal);
}
