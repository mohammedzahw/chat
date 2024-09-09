package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.ChannelDto;
import com.example.chat.chat.dto.ShowChannelDto;
import com.example.chat.chat.model.Channel;
import com.example.chat.chat.model.MessageChannel;
import com.example.chat.chat.service.MessageChannelService;

@Component

public class ChannelMapper {
    private final LocalUserMapper localUserMapper;
    private final MessageChannelMapper messageChannelMapper;
    private final MessageChannelService messageChannelService;

    public ChannelMapper(LocalUserMapper localUserMapper, MessageChannelMapper messageChannelMapper,
            @Lazy MessageChannelService messageChannelService) {
        this.localUserMapper = localUserMapper;
        this.messageChannelService = messageChannelService;
        this.messageChannelMapper = messageChannelMapper;
    }

    public ChannelDto toDto(Channel channel) {
        if (channel == null) {
            return null;
        }

        ChannelDto channelDto = new ChannelDto();

        channelDto.setDescription(channel.getDescription());
        channelDto.setId(channel.getId());
        channelDto.setImageUrl(channel.getImageUrl());
        channelDto.setMessages(messageChannelMapper.toDtoList(channel.getMessages()));
        channelDto.setName(channel.getName());
        channelDto.setOwner(localUserMapper.toDto(channel.getOwner()));

        return channelDto;
    }

    /******************************************************************************** */

    public List<ChannelDto> toDtoList(List<Channel> myChannels) {
        if (myChannels == null) {
            return null;
        }

        List<ChannelDto> list = new ArrayList<ChannelDto>(myChannels.size());
        for (Channel channel : myChannels) {
            list.add(toDto(channel));
        }

        return list;
    }

    /********************************************************************************* */

    public List<ShowChannelDto> toShowDtoList(List<Channel> myChannels) {
        if (myChannels == null) {
            return null;
        }

        List<ShowChannelDto> list = new ArrayList<ShowChannelDto>(myChannels.size());
        for (Channel channel : myChannels) {
            list.add(toShowDto(channel));
        }

        return list;
    }

    /*************************************************************************************************************** */

    private ShowChannelDto toShowDto(Channel channel) {
        if (channel == null) {
            return null;
        }
        ShowChannelDto showChannelDto = new ShowChannelDto();
        // Long unreadMessages =
        // messageChannelService.getNumberOfUreadMessageByChannelId(channel.getId());
        MessageChannel lastMessage = messageChannelService.getLastMessage(channel.getId());
        if (lastMessage != null) {
            showChannelDto.setLastMessageSendDate(lastMessage.getSendDateTime());
            showChannelDto.setLastMessageType(lastMessage.getType());
            showChannelDto.setLastMessageContent(lastMessage.getContent());
        }
        showChannelDto.setId(channel.getId());
        showChannelDto.setName(channel.getName());

        showChannelDto.setImageUrl(channel.getImageUrl());

        return showChannelDto;
    }

    /*************************************************************************************************************** */

}
