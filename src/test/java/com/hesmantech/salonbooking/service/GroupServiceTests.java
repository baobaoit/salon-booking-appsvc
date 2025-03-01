package com.hesmantech.salonbooking.service;

import com.hesmantech.salonbooking.api.dto.group.CreateGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.SearchGroupRequest;
import com.hesmantech.salonbooking.api.dto.group.UpdateGroupRequest;
import com.hesmantech.salonbooking.api.dto.sort.group.GroupSortProperty;
import com.hesmantech.salonbooking.domain.GroupEntity;
import com.hesmantech.salonbooking.exception.group.GroupNotFoundException;
import com.hesmantech.salonbooking.repository.GroupRepository;
import com.hesmantech.salonbooking.service.impl.GroupServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GroupServiceTests {
    @Mock
    private GroupRepository groupRepository;

    private GroupService groupService;

    @BeforeEach
    void setUp() {
        this.groupService = new GroupServiceImpl(groupRepository);
    }

    @Test
    void testCreateGroupSuccess() {
        // given
        var groupName = "Test";
        var request = new CreateGroupRequest(groupName);

        Mockito.when(groupRepository.save(Mockito.any(GroupEntity.class)))
                .thenReturn(GroupEntity.builder()
                        .name(groupName)
                        .build());

        // when
        var result = groupService.create(request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(groupName);
    }

    @Test
    void testGetGroupDetailsFailed() {
        try {
            // given
            Mockito.when(groupRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            groupService.getDetails(UUID.randomUUID());
        } catch (GroupNotFoundException e) {
            Assertions.assertThat(e).isInstanceOf(GroupNotFoundException.class);
        }
    }

    @Test
    void testGetGroupDetailsSuccess() {
        // given
        var groupId = UUID.randomUUID();
        var exampleGroupEntity = GroupEntity.builder()
                .id(groupId)
                .build();

        Mockito.when(groupRepository.findById(groupId))
                .thenReturn(Optional.of(exampleGroupEntity));

        // when
        var result = groupService.getDetails(groupId);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(groupId);
    }

    @Test
    void testUpdateGroupFailed() {
        try {
            // given
            Mockito.when(groupRepository.findById(Mockito.any(UUID.class)))
                    .thenReturn(Optional.empty());

            // when
            groupService.update(UUID.randomUUID(), null);
        } catch (GroupNotFoundException e) {
            Assertions.assertThat(e).isInstanceOf(GroupNotFoundException.class);
        }
    }

    @Test
    void testUpdateGroupSuccess() {
        // given
        var groupId = UUID.randomUUID();
        var exampleGroupEntity = GroupEntity.builder()
                .id(groupId)
                .name("Old group name")
                .build();
        var request = new UpdateGroupRequest("New group name");

        Mockito.when(groupRepository.findById(groupId))
                .thenReturn(Optional.of(exampleGroupEntity));

        Mockito.when(groupRepository.save(exampleGroupEntity))
                .thenReturn(exampleGroupEntity);

        // when
        var result = groupService.update(groupId, request);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(groupId);
        Assertions.assertThat(result.getName()).isEqualTo(request.name());
    }

    @Test
    void testSearchSuccess() {
        // given
        var page = 0;
        var size = 10;
        var direction = Sort.Direction.DESC;
        var property = GroupSortProperty.CREATED_DATE;

        Mockito.when(groupRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Mockito.when(groupRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        // when
        var resultEmpty = groupService.search(page, size, direction, property, new SearchGroupRequest(null));
        var resultTest = groupService.search(page, size, direction, property, new SearchGroupRequest("Test"));

        // then
        Assertions.assertThat(resultEmpty).isNotNull();
        Assertions.assertThat(resultTest).isNotNull();
        Assertions.assertThat(resultEmpty.getContent()).isEmpty();
        Assertions.assertThat(resultTest.getContent()).isEmpty();
    }
}
