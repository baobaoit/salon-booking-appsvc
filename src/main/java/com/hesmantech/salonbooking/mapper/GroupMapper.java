package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.api.dto.group.GroupResponse;
import com.hesmantech.salonbooking.domain.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    GroupResponse toGroupResponse(GroupEntity group);
}
