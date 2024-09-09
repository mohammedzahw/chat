package com.example.chat.chat.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

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
import com.example.chat.chat.service.MessageChannelService;
import com.example.chat.mapper.MessageChannelMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/message-channel")
@RequiredArgsConstructor
public class MessageChannelController {

    private final MessageChannelMapper messageChannelMapper;

    private final MessageChannelService messageChannelService;


    /*************************************************************************************************/

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageDto message) throws IOException, TimeoutException {
        messageChannelService.sendMessage(message);
        return ResponseEntity.ok("Message sent");
    }

    /************************************************************************************************* */
    @PostMapping("/react-message")
    public ResponseEntity<?> reactMessage(@RequestParam("messageId") Integer messageId,
            @RequestParam("reaction") MessageReaction reaction)
            throws IOException, TimeoutException {
        messageChannelService.reactMessage(messageId, reaction);
        return ResponseEntity.ok("Message reacted");
    }

    /*********************************************************************************************** */
    @PostMapping("/unreact-message")
    public ResponseEntity<?> reactMessage(@RequestParam("messageId") Integer messageId
            )
            throws IOException, TimeoutException {
        messageChannelService.unReactMessage(messageId);
        return ResponseEntity.ok("Message Un reacted");
    }

    /*********************************************************************************************** */
    @GetMapping("/get-messages/{channelId}/{page}")
    public ResponseEntity<?> getChannelMessages(@PathVariable("channelId") Integer channelId,
            @PathVariable("page") Integer page) {
        return ResponseEntity.ok(messageChannelMapper.toDtoList(
                messageChannelService.getMessagesByChannelId(channelId, page)));
    }

    /*********************************************************************************************** */
    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") Integer messageId) {
        messageChannelService.deleteMessage(messageId);
        return ResponseEntity.ok("Message deleted");
    }
    
   
}
