package com.example.chat.chat.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.chat.dto.CreateChannelRequestDto;
import com.example.chat.chat.dto.UpdateChannelRequest;
import com.example.chat.chat.service.ChannelService;
import com.example.chat.mapper.ChannelMapper;
import com.example.chat.mapper.LocalUserMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final LocalUserMapper localUserMapper;
    private final ChannelMapper channelMapper;
    private final ChannelService channelService;

    /********************************************************************************************** */

    @PostMapping("/create-channel")
    public ResponseEntity<?> createChannel(@RequestBody CreateChannelRequestDto createChannelRequestDto)
            throws IOException, TimeoutException {
        return ResponseEntity.ok(channelMapper.toDto(channelService.createChannel(createChannelRequestDto)));
    }

    /********************************************************************************************** */
    @PutMapping("/update-channel")
    public ResponseEntity<?> updateChannel(@RequestBody UpdateChannelRequest updateChannelRequestDto)
            throws IOException, TimeoutException {
        channelService.updateChannel(updateChannelRequestDto);
        return ResponseEntity.ok("Channel updated");
    }

    /********************************************************************************************** */
    @GetMapping("/open-channel/{channelId}")
    public ResponseEntity<?> getChannel(@PathVariable("channelId") Integer channelId) {
        return ResponseEntity.ok(channelService.getChannelWithMessages(channelId));
    }

    /********************************************************************************************** */
    @GetMapping("/follow-channel/{channelId}")
    public ResponseEntity<?> followChannel(@PathVariable("channelId") Integer channelId) {
        channelService.followChannel(channelId);
        return ResponseEntity.ok("Channel followed");
    }

    /********************************************************************************************** */
    @GetMapping("/get-followers/{channelId}")
    public ResponseEntity<?> getFollowers(@PathVariable("channelId") Integer channelId) {
        return ResponseEntity.ok(localUserMapper.toDtoList(channelService.getFollowers(channelId)));
    }

    /********************************************************************************************** */

    @GetMapping("/get-channel-owner/{channelId}")
    public ResponseEntity<?> getChannelOwner(@PathVariable("channelId") Integer channelId) {
        return ResponseEntity.ok(localUserMapper.toDto(channelService.getChannelOwner(channelId)));
    }

    /********************************************************************************************** */

    @GetMapping("/get-followed-channels")
    public ResponseEntity<?> getFollowedChannels() {
        return ResponseEntity.ok(channelService.getFollowedChannels());
    }

    /********************************************************************************************** */
    @GetMapping("/get-mychannels")
    public ResponseEntity<?> getMyChannels() {
        return ResponseEntity.ok(channelMapper.toShowDtoList(channelService.getMyChannels()));
    }

    /********************************************************************************************** */
    @DeleteMapping("/delete-channel/{channelId}")
    public ResponseEntity<?> deleteChannel(@PathVariable("channelId") Integer channelId)
            throws IOException, TimeoutException {
        channelService.deleteChannel(channelId);
        return ResponseEntity.ok("Channel deleted");
    }

}
