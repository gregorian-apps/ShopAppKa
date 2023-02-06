package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.GroupExistsException;
import com.shop.list.shopappka.exceptions.GroupNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.GroupRequest;
import com.shop.list.shopappka.repositories.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private GroupService groupService;

    private GroupRequest groupRequest;
    private Group group;

    @BeforeEach
    void setUp() {
        groupRequest = GroupRequest.builder().name("Dummy group").build();
        group = Group.builder().groupId(1L).name("Dummy group").users(null).build();
    }
    @Test
    void shouldCreateNewGroupWhenGroupDoesNotExistThenReturnCreatedGroup() {
        when(groupRepository.getGroupByName(anyString())).thenReturn(Optional.empty());
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group addedGroup = groupService.addNewGroup(groupRequest);

        verify(groupRepository).getGroupByName(anyString());
        verify(groupRepository).save(any());

        assertAll(
                () -> assertEquals(group.getGroupId(), addedGroup.getGroupId()),
                () -> assertEquals(group.getName(), addedGroup.getName()),
                () -> assertEquals(group.getUsers(), addedGroup.getUsers())
        );
    }

    @Test
    void shouldThrownGroupExistsExceptionWhenGroupExists() {
        when(groupRepository.getGroupByName(anyString())).thenReturn(Optional.of(group));
        assertThrows(GroupExistsException.class, () -> groupService.addNewGroup(groupRequest));
    }

    @Test
    void shouldUpdateNameGroupWhenGroupExistsThenReturnUpdatedGroup() {
        GroupRequest updatedGroupRequest = GroupRequest.builder().name("Updated group name").build();
        when(groupRepository.getGroupById(anyLong())).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group updatedGroup = groupService.updateGroup(updatedGroupRequest, 1L);

        assertAll(
                () -> assertEquals(updatedGroupRequest.getName(), updatedGroup.getName()),
                () -> assertEquals(group.getGroupId(), updatedGroup.getGroupId()),
                () -> assertEquals(group.getUsers(), updatedGroup.getUsers())
        );
    }

    @Test
    void shouldThrownGroupNotFoundWhenGroupNotFound() {
        when(groupRepository.getGroupById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GroupNotFoundException.class, () -> groupService.getGroupById(1L));
    }

    @Test
    void shouldDeleteGroupWhenGroupExists() {
        when(groupRepository.getGroupById(anyLong())).thenReturn(Optional.of(group));
        groupService.deleteGroup(1L);
        verify(groupRepository).delete(any(Group.class));
    }

    @Test
    void shouldDeleteUserFromGroupWhenGroupExistsThenReturnGroupWithoutDeletedUser() {
        UserEntity user = UserEntity.builder().userId(1L).firstName("a112").username("a112").email("email@email").password("dummy123").role("ROLE_USER").build();
        UserEntity user1 = UserEntity.builder().userId(2L).firstName("a233").username("a233").email("email@email").password("dummy123").role("ROLE_USER").build();
        Set<UserEntity> setOfUsers = Set.of(user, user1);
        Group group1 = Group.builder().groupId(2L).name("Dummy group").users(setOfUsers).build();
        when(groupRepository.getGroupById(anyLong())).thenReturn(Optional.of(group1));
        when(groupRepository.save(any(Group.class))).thenReturn(group1);

        Group updatedGroup = groupService.deleteUserFromGroup(1L, 2L);

        verify(groupRepository).save(any(Group.class));
        assertEquals(1, updatedGroup.getUsers().size());
        assertEquals(user.getUserId(), updatedGroup.getUsers().iterator().next().getUserId());
    }

    @Test
    void shouldAssignedNewUsersToGroupWhenGroupExistsThenReturnThisGroupWithAssignedUsers() {
        UserEntity user = UserEntity.builder().userId(1L).firstName("a112").username("a112").email("email@email").password("dummy123").role("ROLE_USER").build();
        UserEntity user1 = UserEntity.builder().userId(2L).firstName("a233").username("a233").email("email@email").password("dummy123").role("ROLE_USER").build();
        Set<UserEntity> setOfUsers = Set.of(user, user1);
        when(groupRepository.getGroupById(anyLong())).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(userService.existsUserByUserId(anyLong())).thenReturn(true);

        Group groupWithAssignedUsers = groupService.assignUsersToGroup(setOfUsers, 1L);

        verify(userService, times(2)).existsUserByUserId(anyLong());
        assertAll(
                () -> assertNotNull(groupWithAssignedUsers),
                () -> assertEquals(2, groupWithAssignedUsers.getUsers().size())
        );
    }
}