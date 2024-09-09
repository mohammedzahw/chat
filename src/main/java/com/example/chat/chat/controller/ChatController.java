package com.example.chat.chat.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.chat.service.ChatService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

 

    /******************************************************************************************************* */
    @PostMapping("/create-chat")
    public ResponseEntity<?> createChat(@RequestParam("user2Id") Integer user2Id) throws IOException, TimeoutException {
        chatService.createChat(user2Id);
        return new ResponseEntity<>(chatService.createChat(user2Id), HttpStatus.OK);
    }

    /******************************************************************************************************* */

    @PostMapping("/open-chat/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable("chatId") Integer chatId) throws IOException, TimeoutException {

        return new ResponseEntity<>(chatService.getChatWithMessages(chatId), HttpStatus.OK);
    }

    /******************************************************************************************************* */

    @GetMapping("/user-chats")
    public ResponseEntity<?> getUserChats() throws IOException, TimeoutException {
        return new ResponseEntity<>(chatService.getUserChats(), HttpStatus.OK);
    }

    /********************************************************************************************** */
    @DeleteMapping("/delete-chat/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable("chatId") Integer chatId) throws IOException, TimeoutException {
        chatService.deleteChat(chatId);
        return new ResponseEntity<>("Chat deleted", HttpStatus.OK);
    }   

    /********************************************************************************************** */
}
