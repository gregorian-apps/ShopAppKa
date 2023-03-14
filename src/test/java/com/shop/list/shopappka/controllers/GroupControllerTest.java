package com.shop.list.shopappka.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.list.shopappka.configurations.auth.TokenProvider;
import com.shop.list.shopappka.exceptions.GroupNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.GroupRequest;
import com.shop.list.shopappka.services.GroupService;
import com.shop.list.shopappka.services.JwtUserService;
import com.shop.list.shopappka.services.MapValidationErrorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GroupController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    private final static String API_URL = "/api/data/groups";

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private MapValidationErrorService mapValidationErrorService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private JwtUserService jwtUserService;

    private Group group;
    private Group group1;

    private List<Group> groups;

    @BeforeEach
    void setup() {
        group = Group.builder().groupId(1L).name("Dummy group").users(null).build();
        group1 = Group.builder().groupId(2L).name("Dummy group2").users(null).build();
        groups = List.of(group, group1);
    }

    @Test
    void shouldReturnListOfGroupsWithStatusCode200() throws Exception {
        when(groupService.getAllGroups()).thenReturn(groups);
        mvc.perform(MockMvcRequestBuilders.get(API_URL).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].groupId").value(1L))
                .andExpect(jsonPath("$.[1].groupId").value(2L));
    }

    @Test
    void shouldReturnUserByIdWithStatus200() throws Exception {
        when(groupService.getGroupById(anyLong())).thenReturn(group);
        mvc.perform(MockMvcRequestBuilders.get(API_URL + "/{id}", 1L).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isNotEmpty())
                .andExpect(jsonPath("$.groupId").value(1L))
                .andExpect(jsonPath("$.name").value("Dummy group"))
                .andExpect(jsonPath("$.users").isEmpty());
    }

    @Test
    void shouldReturnInformationAboutGroupNotFoundWithStatus404() throws Exception {
        when(groupService.getGroupById(anyLong())).thenThrow(GroupNotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.get(API_URL + "/{id}", 3L).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddNewGroupThenReturnAddedNewGroupWithStatus201() throws Exception {
        GroupRequest groupRequest = new GroupRequest("Dummy group");
        when(groupService.addNewGroup(groupRequest)).thenReturn(group);
        mvc.perform(MockMvcRequestBuilders.post(API_URL + "/add")
                        .content(mapper.writeValueAsString(groupRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groupId").value(1L))
                .andExpect(jsonPath("$.name").value(groupRequest.getName()));

        verify(groupService).addNewGroup(any(GroupRequest.class));
        verify(mapValidationErrorService).mapValidationError(any(BindingResult.class));
    }

    @Test
    void shouldUpdateExistingGroupThenReturnUpdatedGroupWithStatus200() throws Exception {
        GroupRequest updateGroup = GroupRequest.builder().name("Update group name").build();
        group.setName(updateGroup.getName());
        when(mapValidationErrorService.mapValidationError(any(BindingResult.class))).thenReturn(null);
        when(groupService.updateGroup(updateGroup, 1L)).thenReturn(group);
        mvc.perform((MockMvcRequestBuilders.put(API_URL + "/update/{id}", 1L))
                .content(mapper.writeValueAsString(updateGroup))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateGroup.getName()));
        verify(groupService).updateGroup(any(GroupRequest.class), anyLong());
        verify(mapValidationErrorService).mapValidationError(any(BindingResult.class));
    }

    @Test
    void shouldDeleteGroupWhenGroupExistsThenReturnInformationAboutGroupHasBeenDeletedWithStatus200() throws Exception {
        doNothing().when(groupService).deleteGroup(anyLong());
        mvc.perform(MockMvcRequestBuilders.delete(API_URL + "/delete/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Group has deleted with success"));
        verify(groupService).deleteGroup(anyLong());
    }

    @Test
    void shouldThrowGroupNotFoundExceptionWhenGroupNotFoundThenReturnInfoWithStatus404() throws Exception {
        doThrow(new GroupNotFoundException(group.getName())).when(groupService).deleteGroup(anyLong());
        mvc.perform(MockMvcRequestBuilders.delete(API_URL + "/delete/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAssignNewUsersToTheGroupWhenGroupExistsThenReturnGroupWithAssignedUsersWithStatus200() throws Exception {
        UserEntity user1 = UserEntity.builder().userId(1L).username("Username1").role("ROLE_USER").email("email1@email.com").firstName("Name1").password("password1").build();
        UserEntity user2 = UserEntity.builder().userId(2L).username("Username2").role("ROLE_USER").email("email2@email.com").firstName("Name2").password("password2").build();
        Set<UserEntity> users = Set.of(user1, user2);
        group.setUsers(users);
        when(groupService.assignUsersToGroup(any(), anyLong())).thenReturn(group);
        mvc.perform(MockMvcRequestBuilders.post(API_URL + "/{id}/assign", 1L)
                .content(mapper.writeValueAsString(users))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.size()").value(2));
    }

    @Test
    void shouldDeleteUserFromGroupAndReturnGroupWithRemainingUsersInTheGroupWithStatus200() throws Exception {
        UserEntity user1 = UserEntity.builder().userId(1L).username("Username1").role("ROLE_USER").email("email1@email.com").firstName("Name1").password("password1").build();
        Set<UserEntity> users = Set.of(user1);
        group.setUsers(users);
        when(groupService.deleteUserFromGroup(anyLong(), anyLong())).thenReturn(group);
        mvc.perform(MockMvcRequestBuilders.delete(API_URL + "/delete/1/user/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.size()").value(1));
    }
}