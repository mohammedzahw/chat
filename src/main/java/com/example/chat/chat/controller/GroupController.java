package com.example.chat.chat.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.chat.dto.CreateGroupRequestDto;
import com.example.chat.chat.dto.GroupDto;
import com.example.chat.chat.dto.UpdateGroupRequest;
import com.example.chat.chat.service.GroupService;

import lombok.Data;

@RestController
@Data
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;

    }

    /********************************************************************************************** */
    @PostMapping("/create-group")
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupRequestDto createGroupRequestDto)
            throws IOException, TimeoutException {
        return ResponseEntity.ok(groupService.createGroup(createGroupRequestDto));
    }

    /********************************************************************************************** */
    @PutMapping("/update-group")
    public ResponseEntity<?> updateGroup(@RequestBody UpdateGroupRequest updateGroupRequest)
            throws IOException, TimeoutException {
        groupService.updateGroup(updateGroupRequest);
        return ResponseEntity.ok("Group updated");
    }

    /********************************************************************************************** */
    @GetMapping("/open-group/{groupId}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable("groupId") Integer groupId) {
        return ResponseEntity.ok(groupService.getGroup(groupId));
    }

    /********************************************************************************************** */
    @GetMapping("/join-group/{groupId}")
    public ResponseEntity<GroupDto> joinGroup(@PathVariable("groupId") Integer groupId) {
        return ResponseEntity.ok(groupService.joinGroup(groupId));
    }

    /********************************************************************************************** */
    @GetMapping("/get-joined-groups")
    public ResponseEntity<?> getUserGroups() {
        return ResponseEntity.ok(groupService.getUserGroups());
    }

    /********************************************************************************************** */
    @GetMapping("/get-owned-groups")
    public ResponseEntity<?> getOwnedGroups() {
        return ResponseEntity.ok(groupService.getOwnedGroups());
    }

    /********************************************************************************************** */
    @DeleteMapping("/delete-group/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable("groupId") Integer groupId)
            throws IOException, TimeoutException {
        groupService.deleteGroup(groupId);
        return new ResponseEntity<>("Group deleted", HttpStatus.OK);
    }

}
