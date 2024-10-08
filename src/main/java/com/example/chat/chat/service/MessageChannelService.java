package com.example.chat.chat.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chat.chat.dto.SendMessageDto;
import com.example.chat.chat.dto.SendTextMessageDto;
import com.example.chat.chat.model.Channel;
import com.example.chat.chat.model.MessageChannel;
import com.example.chat.chat.model.MessageChannelReaction;
import com.example.chat.chat.model.MessageReaction;
import com.example.chat.chat.model.MessageType;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.repository.MessageChannelReactionRepository;
import com.example.chat.chat.repository.MessageChannelRepository;
import com.example.chat.exception.CustomException;
import com.example.chat.mapper.MessageChannelMapper;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.model.LocalUser;

import lombok.Data;

@Service
@Data

public class MessageChannelService {
   private final MessageChannelMapper messageChannelMapper;

    private final QueueService queueService;
    private final MessageChannelRepository messageChannelRepository;
    private final MessageChannelReactionRepository messageChannelReactionRepository;

    private final LocalUserService localUserService;
    private final CloudinaryService cloudinaryService;

    private final ChannelService channelService;
    public MessageChannelService( @Lazy MessageChannelMapper messageChannelMapper,
        QueueService queueService, MessageChannelRepository messageChannelRepository,
        MessageChannelReactionRepository messageChannelReactionRepository,
            LocalUserService localUserService, ChannelService channelService,
            CloudinaryService cloudinaryService) {

        this.cloudinaryService = cloudinaryService;

        this.messageChannelMapper = messageChannelMapper;
        this.queueService = queueService;
        this.messageChannelRepository = messageChannelRepository;
        this.messageChannelReactionRepository = messageChannelReactionRepository;
        this.localUserService = localUserService;
        this.channelService = channelService;
        }

    /************************************************************************************************ */

    @Transactional
    public void sendMediaMessage(SendMessageDto sendMessageDto)
            throws IOException, TimeoutException {

        Channel channel = channelService.getChannel(sendMessageDto.getId());
        Queue queue = channel.getQueue();
        LocalUser user = localUserService.getLocalUserByToken();
        if (!user.getId().equals(channel.getOwner().getId())) {
            throw new CustomException("You are not owner of this channel", HttpStatus.BAD_REQUEST);
        }
        Map result = cloudinaryService.upload(sendMessageDto.getFile(),channel.getName() + channel.getId());

        String url = (String) result.get("url");
        String publicId = (String) result.get("public_id");
        Double duration = 0.0;
        if (sendMessageDto.getType().equals(MessageType.VIDEO)
                || sendMessageDto.getType().equals(MessageType.AUDIO))
            duration = Double.parseDouble(result.get("duration").toString());
        Double size = Double.parseDouble(result.get("bytes").toString());
        MessageChannel messageChannel = messageChannelMapper.toMediaEntity(sendMessageDto, channel,
                url, publicId, duration, size);
        messageChannelRepository.save(messageChannel);
        channel.setLastUpdated(LocalDateTime.now());
        channelService.saveChannel(channel);

        queueService.sendMessage(queue,
                messageChannelMapper.toDto(messageChannel), "Channel");

    }

    /**
     * @throws TimeoutException
     * @throws IOException
     *********************************************************************************************/

    public void sendTextMessage(SendTextMessageDto sendMessageDto) throws IOException, TimeoutException {

        Channel channel = channelService.getChannel(sendMessageDto.getId());
        Queue queue = channel.getQueue();
        LocalUser user = localUserService.getLocalUserByToken();
        if (!user.getId().equals(channel.getOwner().getId())) {
            throw new CustomException("You are not owner of this channel", HttpStatus.BAD_REQUEST);
        }

        MessageChannel messageChannel = messageChannelMapper.toEntity(sendMessageDto, channel);
        messageChannelRepository.save(messageChannel);
        channel.setLastUpdated(LocalDateTime.now());
        channelService.saveChannel(channel);

        queueService.sendMessage(queue,
                messageChannelMapper.toDto(messageChannel), "Channel");
    }

    /***********************************************************************************************/
    public MessageChannel getMessageById(Integer id) {
        return messageChannelRepository.findById(id).orElseThrow(
            () -> new CustomException("Message not found", HttpStatus.NOT_FOUND));
        }
        /***********************************************************************************************/

    public void reactMessage(Integer messageId, MessageReaction reaction) {
        LocalUser user = localUserService.getLocalUserByToken();
        MessageChannelReaction messageChannelReaction = getReactionByMessageIdAndUserId(messageId, user.getId());
        if (messageChannelReaction == null) {
            messageChannelReaction = new MessageChannelReaction();
            messageChannelReaction.setMessageChannel(getMessageById(messageId));
            messageChannelReaction.setReactor(user);
            messageChannelReaction.setReaction(reaction);
        } else
            messageChannelReaction.setReaction(reaction);
        messageChannelReactionRepository.save(messageChannelReaction);

    }

    /********************************************************************** */
    
    public MessageChannelReaction getReactionByMessageIdAndUserId(Integer messageId, Integer userId) {
        return messageChannelReactionRepository
        .getReactionByMessageIdAndUserId(messageId, userId).orElse(null);
    }
    /********************************************************************** */
    
    public MessageChannel getLastMessage(Integer id) {
        
        List<MessageChannel> messages = messageChannelRepository.getLastMessage(id, PageRequest.of(0, 1));
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(0);
    }
    /********************************************************************** */
    
    public List<MessageChannel> getMessagesByChannelId(Integer channelId, Integer pageNumber) {
        
        return messageChannelRepository.getMessagesByChannelId(channelId, PageRequest.of(pageNumber, 20));
    }
    /********************************************************************** */

    public void unReactMessage(Integer messageId) {

        LocalUser user = localUserService.getLocalUserByToken();
        MessageChannelReaction messageChannelReaction = getReactionByMessageIdAndUserId(messageId, user.getId());
        if (messageChannelReaction != null) {
            messageChannelReactionRepository.delete(messageChannelReaction);
        }

    }

    /**
     * @throws IOException ******************************************************************************************************************* */
    @Transactional
     public void deleteMessage(Integer messageId) throws IOException {

        MessageChannel messageChannel = getMessageById(messageId);

        if (messageChannel.getType() != MessageType.TEXT) {
            cloudinaryService.delete(messageChannel.getUrlId());
        }

        messageChannelRepository.deleteById(messageId);
    }

}
