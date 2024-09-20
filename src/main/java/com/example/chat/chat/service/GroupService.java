package com.example.chat.chat.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.chat.chat.dto.CreateGroupRequestDto;
import com.example.chat.chat.dto.GroupDto;
import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.dto.ShowChatDto;
import com.example.chat.chat.dto.UpdateGroupRequest;
import com.example.chat.chat.model.Group;
import com.example.chat.chat.model.ImageGroup;
import com.example.chat.chat.model.MessageGroup;
import com.example.chat.chat.model.MessageGroupReceivedTime;
import com.example.chat.chat.model.MessageGroupStatus;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.repository.GroupRepository;
import com.example.chat.chat.repository.ImageGroupRepository;
import com.example.chat.exception.CustomException;
import com.example.chat.mapper.GroupMapper;
import com.example.chat.mapper.MessageGroupMapper;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.model.LocalUser;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GroupService {

    private final GroupMapper groupMapper;
    private final MessageGroupMapper messageGroupMapper;
    private final ImageGroupRepository imageGroupRepository;

    private final QueueService queueService;

    private final LocalUserService localUserService;

    private final GroupRepository groupRepository;
    private final CloudinaryService cloudinaryService;

    private final MessageGroupService messageGroupService;

    public GroupService(GroupMapper groupMapper, MessageGroupMapper messageGroupMapper, QueueService queueService,
            LocalUserService localUserService, GroupRepository groupRepository,
            CloudinaryService cloudinaryService,
            ImageGroupRepository imageGroupRepository,
            @Lazy MessageGroupService messageGroupService) {
        this.groupMapper = groupMapper;
        this.cloudinaryService = cloudinaryService;
        this.messageGroupMapper = messageGroupMapper;
        this.imageGroupRepository = imageGroupRepository;
        this.queueService = queueService;
        this.localUserService = localUserService;
        this.groupRepository = groupRepository;
        this.messageGroupService = messageGroupService;
    }

    /**
     * @throws TimeoutException
     *                          ****************************************************************************************
     */

    @Transactional
    public GroupDto createGroup(CreateGroupRequestDto createGroupRequestDto) throws IOException, TimeoutException {
        {

            LocalUser owner = localUserService.getLocalUserByToken();
            Group group = new Group();
            group.setName(createGroupRequestDto.getName());
            group.setDescription(createGroupRequestDto.getDescription());
            group.setPrivacy(createGroupRequestDto.getPrivacy());
            group.setOwner(owner);
            // group.setMembers(List.of(owner));
            group.setCreatedDate(LocalDateTime.now());
            group.setLastUpdated(LocalDateTime.now());

            groupRepository.save(group);

            Queue queue = queueService.getQueue(group.getName() + group.getId());
            if (queue == null) {

                queue = queueService.createQueue(group.getName() + group.getId(), group.getName() + group.getId(),
                        group.getName() + group.getId());

            }

            group.setQueue(queue);
            groupRepository.save(group);

            localUserService.addQueue(owner.getId(), queue.getId());

            return groupMapper.toDto(group);
        }
    }

    /****************************************************************************************** */
    public GroupDto getGroup(Integer groupId) {
        Group group = getGroupById(groupId);
        LocalUser owner = getGroupOwner(groupId);

        List<MessageGroup> messages = groupRepository.getMessagesByGroupId(groupId, PageRequest.of(0, 20));
        group.setOwner(owner);

        group.setMessages(messages);
        return groupMapper.toDto(group);
    }

    /********************************************************************** */
    public GroupDto joinGroup(Integer groupId) {
        Group group = getGroupById(groupId);

        LocalUser member = localUserService.getLocalUserByToken();
        LocalUser owner = getGroupOwner(groupId);
        if (owner.getId().equals(member.getId())) {
            throw new CustomException("You Can't Join Your Own Group", HttpStatus.BAD_REQUEST);
        }
        if (group.getPrivacy().equals("private")) {
            /// TODO: Add a check for the user to be invited to the group

            throw new CustomException("You Can't Join Private Group", HttpStatus.BAD_REQUEST);
        }
        List<LocalUser> members = groupRepository.getMembersByGroupId(groupId);
        if (members.contains(member)) {
            throw new CustomException("User already in group", HttpStatus.BAD_REQUEST);
        }
        members.add(member);
        group.setMembers(members);
        groupRepository.save(group);

        localUserService.addQueue(member.getId(), group.getQueue().getId());
        return groupMapper.toDto(group);
    }

    /******************************************************************************************************* */
    public LocalUser getGroupOwner(Integer groupId) {

        LocalUser owner = groupRepository.getOwnerByGroupId(groupId).orElseThrow(
                () -> new CustomException("Owner not found", HttpStatus.NOT_FOUND));
        return owner;
    }

    /******************************************************************************************************* */
    public List<LocalUser> getGroupAdmins(Integer groupId) {
        List<LocalUser> admins = groupRepository.getAdminsByGroupId(groupId);
        return admins;
    }

    /****************************************************************************************** */

    public List<LocalUser> getGroupMembers(Integer groupId) {
        List<LocalUser> members = groupRepository.getMembersByGroupId(groupId);
        return members;
    }

    /****************************************************************************************** */
    public Group getGroupById(Integer groupId) {

        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new CustomException("Group not found", HttpStatus.NOT_FOUND));
        return group;
    }

    /****************************************************************************************** */
    public void uploadImage(Integer groupId, MultipartFile image) throws IOException {
        LocalUser user = localUserService.getLocalUserByToken();

        BufferedImage bi = ImageIO.read(image.getInputStream());
        if (bi == null) {
            throw new CustomException("Invalid image file", HttpStatus.BAD_REQUEST);
        }
        Group group = getGroupById(groupId);

        if (!user.getId().equals(group.getOwner().getId())) {
            throw new CustomException("You can't upload image to this group", HttpStatus.BAD_REQUEST);
        }
        if (group.getImageGroup() != null) {
            cloudinaryService.delete(group.getImageGroup().getImageId());
        }
        Map result = cloudinaryService.upload(image, group.getName() + groupId);   
        ImageGroup imageGroup = new ImageGroup();
        imageGroup.setImageId((String) result.get("public_id"));
        imageGroup.setImageUrl((String) result.get("url"));

        imageGroup.setGroup(group);

        saveImage(imageGroup);

    }

    /****************************************************************************************** */

    public ImageGroup saveImage(ImageGroup imageGroup) {

        return imageGroupRepository.save(imageGroup);
    }

    /****************************************************************************************** */

    public List<ShowChatDto> getUserGroups() {

        LocalUser user = localUserService.getLocalUserByToken();

        List<Group> groups = groupRepository.getGroupsByMemberId(user.getId());
        if (groups == null) {
            return new ArrayList<>();

        }
        List<ShowChatDto> chatDtos = new ArrayList<>();

        for (Group g : groups) {

            MessageGroup messageGroup = messageGroupService.getLastMessageByGroupId(g.getId());

            MessageDto messageDto = null;
            if (messageGroup != null) {
                messageDto = messageGroupMapper.toDto(messageGroup);
            }
            Long unreadMessages = messageGroupService.getNumberOfUreadMessageByGroupId(g.getId());
            ShowChatDto chatDto = new ShowChatDto(g.getId(), g.getName(), 
                    unreadMessages, messageDto);
            if(g.getImageGroup() != null) {
                chatDto.setImageUrl(g.getImageGroup().getImageUrl());
            }
            chatDtos.add(chatDto);
        }
        return chatDtos;

    }

    /****************************************************************************************** */

    public List<MessageDto> getGroupMessages(Integer groupId, Integer pageNumber) {
        LocalUser user = localUserService.getLocalUserByToken();

        List<MessageGroup> messages = messageGroupService.getMessagesByGroupId(groupId, pageNumber);
        List<MessageDto> messageDtos = new ArrayList<>();
        for (MessageGroup m : messages) {

            MessageGroupStatus status = messageGroupService.getMessageGroupStatusByReceiverId(m.getId(),
                    user.getId());
            MessageGroupReceivedTime messageGroupReceivedTime = messageGroupService
                    .getMessageGroupReceivedTimeByMessageGroupIdAndReceiverId(m.getId(), user.getId());

            MessageDto messageDto = messageGroupMapper.toDto(m);
            messageDto.setStatus(status.getStatus());
            messageDto.setReceiveDateTime(messageGroupReceivedTime.getTime());
            messageDto.setReactions(
                    messageGroupService.getMessageGroupReactionsByMessageGroupId(m.getId())

            );
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    /****************************************************************************************** */

    public List<Group> getOwnedGroups() {
        LocalUser user = localUserService.getLocalUserByToken();
        return groupRepository.getGroupsByOwnerId(user.getId());
    }

    public void saveGroup(Group group) {

        groupRepository.save(group);
    }

    public void updateGroup(UpdateGroupRequest updateGroupRequest) {

        Group group = getGroupById(updateGroupRequest.getId());
        group.setName(updateGroupRequest.getName());
        group.setDescription(updateGroupRequest.getDescription());
        group.setPrivacy(updateGroupRequest.getPrivacy());
        group.setLastUpdated(LocalDateTime.now());
        groupRepository.save(group);
    }

    /**
     * @throws Exception ***************************************************************************************
     */
    @Transactional
    public void deleteGroup(Integer groupId) throws Exception {
        LocalUser user = localUserService.getLocalUserByToken();
        LocalUser owner = getGroupOwner(groupId);
        if (!user.getId().equals(owner.getId())) {
            throw new CustomException("You can't delete this group", HttpStatus.BAD_REQUEST);
        }
        groupRepository.deleteGroupFromMembers(groupId);
        groupRepository.deleteGroupFromAdmins(groupId);
        groupRepository.deleteGroupMessages(groupId);
        Group group = getGroupById(groupId);
        Queue queue = group.getQueue();
        groupRepository.delete(group);
        queueService.deleteQueueFromRabbitMq(queue.getName());
        queueService.deleteQueue(queue.getId());
        cloudinaryService.deleteByFolder(group.getName() + groupId);

        groupRepository.delete(group);
    }
}
