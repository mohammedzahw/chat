package com.example.chat.chat.rabbitmq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.chat.chat.config.JacksonConfig;
import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.model.MessageChat;
import com.example.chat.chat.model.MessageGroup;
import com.example.chat.chat.model.MessageStatus;
import com.example.chat.chat.service.ChatService;
import com.example.chat.chat.service.MessageChannelService;
import com.example.chat.chat.service.MessageChatService;
import com.example.chat.chat.service.MessageGroupService;
import com.example.chat.registration.Service.LocalUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConsumeMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageChatService messageChatService;
    private final MessageGroupService messageGroupService;
    private final MessageChannelService messageChannelService;

    private final LocalUserService localUserService;
    private final RabbitTemplate rabbitTemplate;

    private final ChatService chatService;

    /************************************************************************************************************/

    @RabbitListener(queues = "#{queueList}", containerFactory = "container")
    public void consumeMessageListener(String messageBody) throws JsonMappingException, JsonProcessingException {
        // ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Delay processing by 5 seconds
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted", e);
        }

        setMessage(messageBody);
        // String jsonMessage = "{\"content\":\"" + messageBody + "\"}";

        // JsonNode jsonNode = objectMapper.readTree(jsonMessage);
        // Integer messageId = jsonNode.get("id").asInt();
        // System.out.println("Message ID: " + messageId);

        // messagingTemplate.convertAndSend("/topic/chat", jsonMessage);
        // log.info("Consumed Message: " + messageBody);
    }

    /************************************************************************************************************/

    public void consumeMessagesFromQueues() throws JsonMappingException, JsonProcessingException {
        List<String> queueList = chatService.getQueueList();
        for (String queueName : queueList) {
            consumeAllMessages(queueName);
        }
    }

    /************************************************************************************************************/

    private void consumeAllMessages(String queueName) throws JsonMappingException, JsonProcessingException {
        String messageBody;
       
        while ((messageBody = consumeMessage(queueName)) != null) {

            setMessage(messageBody);
        }
        // log.info("No more messages in the queue: {}", queueName);
    }

    /**
     * @throws JsonProcessingException
     * @throws JsonMappingException
     **********************************************************************************************************/
    public void setMessage(String messageBody) throws JsonMappingException, JsonProcessingException {
        ObjectMapper objectMapper = JacksonConfig.createObjectMapper();

        Map<String, Object> messageMap = objectMapper.readValue(messageBody, Map.class);

        MessageDto messageDto = objectMapper.readValue(messageMap.get("message").toString(), MessageDto.class);

        String type = messageMap.get("type").toString();

        if (type.equals("Chat")) {
            MessageChat messageChat = messageChatService.getMessageById(messageDto.getId());
            messageChat.setStatus(MessageStatus.RECEIVED);
            messageChat.setReceiveDateTime(LocalDateTime.now());
            messageChatService.saveMessageChat(messageChat);

            messagingTemplate.convertAndSend("/topic/chat", messageDto);
        }
        if (type.equals("Group")) {
            MessageGroup messageGroup = messageGroupService.getMessageById(messageDto.getId());
            messageGroupService.setMessageGroupStatus(messageGroup, localUserService.getLocalUserByToken(),
                    MessageStatus.RECEIVED);
            messageGroupService.setReceiveDateTime(messageGroup, localUserService.getLocalUserByToken());

            messageGroupService.saveMessageGroup(messageGroup);

            messagingTemplate.convertAndSend("/topic/group", messageDto);
        } else {
            // MessageChannel messageChannel =
            // messageChannelService.getMessageById(message.getId());
            messagingTemplate.convertAndSend("/topic/channel", messageDto);

        }

    }

    /************************************************************************************************************/
    private String consumeMessage(String queueName) {
        try {
            return (String) rabbitTemplate.receiveAndConvert(queueName);
        } catch (Exception e) {
            log.error("Failed to consume message from queue: {}", queueName, e);
            return null;
        }
    }
}