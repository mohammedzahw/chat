package com.example.chat.chat.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.chat.chat.dto.ChannelDto;
import com.example.chat.chat.dto.CreateChannelRequestDto;
import com.example.chat.chat.dto.ShowChannelDto;
import com.example.chat.chat.dto.UpdateChannelRequest;
import com.example.chat.chat.model.Channel;
import com.example.chat.chat.model.MessageChannel;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.repository.ChannelRepository;
import com.example.chat.exception.CustomException;
import com.example.chat.mapper.ChannelMapper;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.model.LocalUser;

import jakarta.transaction.Transactional;

@Service

public class ChannelService {

    private final ChannelMapper channelMapper;
    private final QueueService queueService;

    private final ChannelRepository channelRepository;

    private final LocalUserService localUserService;

    private final MessageChannelService messageChannelService;

    public ChannelService(ChannelMapper channelMapper, QueueService queueService, ChannelRepository channelRepository,
            LocalUserService localUserService, @Lazy MessageChannelService messageChannelService) {
        this.channelMapper = channelMapper;
        this.queueService = queueService;
        this.channelRepository = channelRepository;
        this.localUserService = localUserService;
        this.messageChannelService = messageChannelService;
    }

    /******************************************************************************************************* */
    @Transactional
    public Channel createChannel(CreateChannelRequestDto createChannelRequestDto)
            throws IOException, TimeoutException {
        {

            LocalUser owner = localUserService.getLocalUserByToken();
            Channel channel = new Channel();
            channel.setName(createChannelRequestDto.getName());
            channel.setDescription(createChannelRequestDto.getDescription());
            channel.setOwner(owner);

            channelRepository.save(channel);
            Queue queue = queueService.getQueue(channel.getName() + channel.getId());
            if (queue == null) {

                queue = queueService.createQueue(channel.getName() + channel.getId(),
                        channel.getName() + channel.getId(),
                        channel.getName() + channel.getId());

            }

            channel.setQueue(queue);
            channel.setCreatedDate(LocalDateTime.now());
            channel.setLastUpdated(LocalDateTime.now());
            channelRepository.save(channel);

            localUserService.addQueue(owner.getId(), queue.getId());

            return channel;
        }

    }

    /******************************************************************************************************* */
    public ChannelDto getChannelWithMessages(Integer channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new CustomException("Channel not found", HttpStatus.NOT_FOUND));

        List<MessageChannel> messages = messageChannelService.getMessagesByChannelId(channelId, 0);
        channel.setMessages(messages);
        return channelMapper.toDto(channel);

    }

    /******************************************************************************************************* */
    public Channel getChannel(Integer channelId) {
        return channelRepository.findById(channelId).orElseThrow(
                () -> new CustomException("Channel not found", HttpStatus.NOT_FOUND));

    }

    /************************************************************************************************************* */
    public LocalUser getChannelOwner(Integer channelId) {
        return channelRepository.getOwnerByChannelId(channelId).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }

    /************************************************************************************************************* */
    public List<LocalUser> getFollowers(Integer channelId) {
        LocalUser owner = getChannelOwner(channelId);
        if (owner.getId() != localUserService.getUserId()) {

            throw new CustomException("User not authorized", HttpStatus.FORBIDDEN);
        }
        return channelRepository.getFollowersByChannelId(channelId);
    }

    /******************************************************************************************************* */
    public void followChannel(Integer channelId) {
        Channel channel = getChannel(channelId);
        LocalUser user = localUserService.getLocalUserByToken();

        LocalUser owner = getChannelOwner(channelId);

        if (user.getId().equals(owner.getId())) {
            throw new CustomException("You Can't Follow Your Own Channel", HttpStatus.BAD_REQUEST);
        }

        if (channelRepository.isUserInChannel(channelId, user.getId())) {

            throw new CustomException("User already in channel", HttpStatus.BAD_REQUEST);
        }
        channelRepository.addFollower(channelId, user.getId());
        channelRepository.save(channel);

        localUserService.addQueue(user.getId(), channel.getQueue().getId());

    }

    /******************************************************************************************************* */
    public List<ShowChannelDto> getFollowedChannels() {

        List<Channel> channels = channelRepository.getFollowedChannelsByUserId(localUserService.getUserId());
        
        return channelMapper.toShowDtoList(channels);

    }

    /******************************************************************************************************* */

    public void saveChannel(Channel channel) {

        channelRepository.save(channel);
    }

    /******************************************************************************************************* */
    /******************************************************************************************************* */

    public List<Channel> getMyChannels() {

        return channelRepository.getChannelsByOwnerId(localUserService.getUserId());
    }

    /******************************************************************************************************* */
    public Channel getChannelById(Integer id) {
        
        return channelRepository.findById(id)
        .orElseThrow(() -> new CustomException("Channel not found", HttpStatus.NOT_FOUND));
    }
    
    public Channel updateChannel(UpdateChannelRequest updateChannelRequestDto) {
        
        Channel channel = getChannel(updateChannelRequestDto.getId());
        channel.setName(updateChannelRequestDto.getName());
        channel.setDescription(updateChannelRequestDto.getDescription());
        channel.setLastUpdated(LocalDateTime.now());
        channelRepository.save(channel);
        return channel;
    }
    
    /******************************************************************************************************* */
    public void deleteChannel(Integer channelId) throws IOException, TimeoutException {
        LocalUser user = localUserService.getLocalUserByToken();

        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new CustomException("Channel not found", HttpStatus.NOT_FOUND));
        if (channel.getOwner().getId() != user.getId()) {
            throw new CustomException("You can't delete this channel", HttpStatus.BAD_REQUEST);
        }

        channelRepository.deleteChannelFromFollowers(channelId);
        Queue queue = channel.getQueue();
        channelRepository.deleteById(channelId);
        queueService.deleteQueueFromRabbitMq(queue.getName());
        queueService.deleteQueue(queue.getId());
    }

    /******************************************************************************************************* */
}
