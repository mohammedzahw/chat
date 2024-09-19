package com.example.chat.chat.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.chat.dto.SendMessageDto;
import com.example.chat.chat.model.MessageReaction;
import com.example.chat.chat.service.MessageGroupService;
import com.example.chat.mapper.MessageGroupMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/message-group")
@RequiredArgsConstructor
public class MessageGroupController {

    private final MessageGroupMapper messageGroupMapper;

    private final MessageGroupService messageGroupService;

    /********************************************************************************************** */
    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageDto sendMessageDto)
            throws IOException, TimeoutException {
        messageGroupService.sendMessage(sendMessageDto);
        return new ResponseEntity<>("Message sent", HttpStatus.OK);
    }

    /********************************************************************************************** */

    @PostMapping("/react-message")
    public ResponseEntity<?> reactMessage(@RequestParam("messageId") Integer messageId,
            @RequestParam("reaction") MessageReaction reaction)
            throws IOException, TimeoutException {
        messageGroupService.reactMessage(messageId, reaction);
        return new ResponseEntity<>("Message sent", HttpStatus.OK);
    }

    /*********************************************************************************************** */
    @PostMapping("/unreact-message")
    public ResponseEntity<?> reactMessage(@RequestParam("messageId") Integer messageId)
            throws IOException, TimeoutException {
        messageGroupService.unReactMessage(messageId);
        return ResponseEntity.ok("Message Un reacted");
    }

    /********************************************************************************************** */

    @GetMapping("/get-messages/{groupId}/{page}")
    public ResponseEntity<?> getGroupMessages(@PathVariable("groupId") Integer groupId,
            @PathVariable("page") Integer page) {
        return ResponseEntity.ok(messageGroupMapper.toDtoList(
                messageGroupService.getMessagesByGroupId(groupId, page)));
    }

    /***************************************************************************************** */
    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") Integer messageId) {
        messageGroupService.deleteMessage(messageId);
        return ResponseEntity.ok("Message deleted");
    }

}
