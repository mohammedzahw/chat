package com.example.chat.chat.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.chat.chat.config.JacksonConfig;
import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.model.Queue;
import com.example.chat.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProduceMessageService {

    private final RabbitTemplate rabbitTemplate;

    private final SimpMessagingTemplate messagingTemplate;

    public ProduceMessageService(RabbitTemplate rabbitTemplate, SimpMessagingTemplate messagingTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @throws JsonProcessingException *********************************************************************************************** */

    @Transactional

    public String produceMessage(Queue queue, MessageDto message, String messageType) throws JsonProcessingException {
           ObjectMapper objectMapper = JacksonConfig.createObjectMapper();
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(message);
        // System.out.println(json);
        try {
            Map<String, Object> messageMap = new HashMap<>();
         
            messageMap.put("type", messageType);
            messageMap.put("message", json);
            String jsonMessage = objectMapper.writeValueAsString(messageMap);

            rabbitTemplate.convertAndSend(queue.getExchange(), queue.getRoutingKey(), jsonMessage);

            messagingTemplate.convertAndSend("/topic/chat", jsonMessage);

            return "Message(" + jsonMessage + ")" + " has been produced.";
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}