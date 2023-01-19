package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.GroupExistsException;
import com.shop.list.shopappka.exceptions.GroupNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.payload.GroupRequest;
import com.shop.list.shopappka.repositories.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
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

        if (group != null) {
            group.setName(groupRequest.getName());
            log.info("Group with id {} has updated successfully", group.getId());
            return groupRepository.save(group);
        }

        return null;
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
}
