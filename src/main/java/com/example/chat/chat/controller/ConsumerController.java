package com.example.chat.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.chat.rabbitmq.ConsumeMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class ConsumerController {

    private final ConsumeMessageService consumerService;

    public ConsumerController(ConsumeMessageService consumerService) {
        this.consumerService = consumerService;
    }

    /********************************************************************************************** */
    @GetMapping("/consume-messages")
    public ResponseEntity<?> consumeMessages() throws JsonMappingException, JsonProcessingException {
        consumerService.consumeMessagesFromQueues();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /********************************************************************************************** */
}
