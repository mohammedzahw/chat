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
import org.springframework.web.multipart.MultipartFile;

import com.example.chat.chat.dto.SendMessageDto;
import com.example.chat.chat.dto.SendTextMessageDto;
import com.example.chat.chat.model.MessageReaction;
import com.example.chat.chat.service.MessageChatService;
import com.example.chat.mapper.MessageChatMapper;

@RestController
@RequestMapping("/message-chat")
public class MessageChatController {
    private MessageChatMapper messageChatMapper;
    private final MessageChatService messageChatService;

    public MessageChatController(MessageChatService messageChatService) {
        this.messageChatService = messageChatService;
    }

    /*************************************************************************************************/

    @PostMapping("/send-message/media")
    public ResponseEntity<?> sendMediaMessage(
            @RequestBody SendMessageDto message)
            throws IOException, TimeoutException {
        messageChatService.sendMediaMessage( message);
        return ResponseEntity.ok("Message sent");
    }

    @PostMapping("/send-message/text")
    public ResponseEntity<?> sendTextMessage(@RequestBody SendTextMessageDto message)
            throws IOException, TimeoutException {
        messageChatService.sendTextMessage(message);
        return ResponseEntity.ok("Message sent");
    }

    /************************************************************************************************* */
    @PostMapping("/react-message")
    public ResponseEntity<?> reactMessage(@RequestParam("messageId") Integer messageId,
            @RequestParam("reaction") MessageReaction reaction)
            throws IOException, TimeoutException {
        messageChatService.reactMessage(messageId, reaction);
        return ResponseEntity.ok("Message reacted");
    }

    /*********************************************************************************************** */
    @PostMapping("/unreact-message")
    public ResponseEntity<?> reactMessage(@RequestParam("messageId") Integer messageId)
            throws IOException, TimeoutException {
        messageChatService.unReactMessage(messageId);
        return ResponseEntity.ok("Message Un reacted");
    }

    /*********************************************************************************************** */
    @GetMapping("/get-messages/{chatId}/{page}")
    public ResponseEntity<?> getChatMessages(@PathVariable("chatId") Integer chatId,
                    @PathVariable("page") Integer page) {
        return ResponseEntity.ok(messageChatMapper.toDtoList(
                messageChatService.getMessagesByChatId(chatId, page)));
    }

    /**
     * @throws IOException ********************************************************************************************* */
    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") Integer messageId) throws IOException {
        messageChatService.deleteMessage(messageId);
        return ResponseEntity.ok("Message deleted");
    }
    /*********************************************************************************************** */
}
