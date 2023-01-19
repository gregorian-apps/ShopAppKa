package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.payload.GroupRequest;
import com.shop.list.shopappka.services.GroupService;
import com.shop.list.shopappka.services.MapValidationErrorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/groups")
public class GroupController {

    private final GroupService groupService;

    private final MapValidationErrorService mapValidationErrorService;

    public GroupController(GroupService groupService, MapValidationErrorService mapValidationErrorService) {
        this.groupService = groupService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        Group group = groupService.getGroupById(id);

        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<?> addNewGroup(@RequestBody @Valid GroupRequest groupRequest, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);

        if (errorMap != null) {
            return errorMap;
        }

        Group group = groupService.addNewGroup(groupRequest);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateGroup(@RequestBody @Valid GroupRequest groupRequest, @PathVariable Long id, BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);

        if (errorMap != null) {
            return errorMap;
        }

        Group group = groupService.updateGroup(groupRequest, id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return new ResponseEntity<>("Group has deleted with success", HttpStatus.OK);
    }
}
