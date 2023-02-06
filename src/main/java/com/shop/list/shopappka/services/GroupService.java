package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.GroupExistsException;
import com.shop.list.shopappka.exceptions.GroupNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.payload.GroupRequest;
import com.shop.list.shopappka.repositories.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;

    private final UserService userService;

    public GroupService(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    public Group addNewGroup(GroupRequest groupRequest) {
        Optional<Group> groupExists = groupRepository.getGroupByName(groupRequest.getName());
        if (groupExists.isEmpty()) {
            Group group = Group.builder()
                    .name(groupRequest.getName())
                    .build();
            log.info("New group with name {} has created successfully", groupRequest.getName());
            return groupRepository.save(group);
        } else {
            String name = groupExists.get().getName();
            log.warn("Group with name {} exists", name);
            throw new GroupExistsException("Group with name: " + name);
        }
    }

    public Group updateGroup(GroupRequest groupRequest, Long id) {
        Group group = getGroupById(id);

        group.setName(groupRequest.getName());
        log.info("Group with id {} has updated successfully", group.getGroupId());
        return groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        Group group = getGroupById(id);
        if (group != null) {
            log.info("Group with name {} has deleted successfully", group.getName());
            groupRepository.delete(group);
        }
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.getGroupById(id).orElseThrow(() -> {
            log.warn("Group with id {} not found", id);
            throw new GroupNotFoundException("Group with id: " + id + " not found");
        });
    }

    public Group assignUsersToGroup(Set<UserEntity> users, Long groupId) {
        Group group = getGroupById(groupId);
        Set<UserEntity> userEntities = users.stream()
                .filter(user -> userService.existsUserByUserId(user.getUserId()))
                .collect(Collectors.toSet());
        group.setUsers(userEntities);
        return groupRepository.save(group);
    }

    public Group deleteUserFromGroup(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        Set<UserEntity> userEntities = group.getUsers().stream()
                .filter(user -> !Objects.equals(user.getUserId(), userId))
                .collect(Collectors.toSet());
        group.setUsers(userEntities);
        return groupRepository.save(group);
    }
}
