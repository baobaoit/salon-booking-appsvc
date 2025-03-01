package com.hesmantech.salonbooking.mapper;

import com.hesmantech.salonbooking.domain.GroupEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class GroupMapperTests {
    private static final GroupMapper groupMapper = GroupMapper.INSTANCE;

    @Test
    void testMapToGroupResponse() {
        // given
        var groupEntity = new GroupEntity();
        var groupId = UUID.randomUUID();
        groupEntity.setId(groupId);
        groupEntity.setName("Test Group");

        // when
        var result = groupMapper.toGroupResponse(groupEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(groupId.toString());
        Assertions.assertThat(result.name()).isEqualTo(groupEntity.getName());
    }

    @Test
    void testGroupIsNull() {
        // when
        var result = groupMapper.toGroupResponse(null);

        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    void testGroupIdIsNull() {
        // given
        var groupEntity = new GroupEntity();

        // when
        var result = groupMapper.toGroupResponse(groupEntity);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNull();
    }
}
