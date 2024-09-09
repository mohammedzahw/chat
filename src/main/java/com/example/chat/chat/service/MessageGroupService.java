package com.example.chat.chat.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.chat.chat.dto.SendMessageDto;
import com.example.chat.chat.dto.UpdateMessageDto;
import com.example.chat.chat.model.Group;
import com.example.chat.chat.model.MessageGroup;
import com.example.chat.chat.model.MessageGroupReaction;
import com.example.chat.chat.model.MessageGroupReceivedTime;
import com.example.chat.chat.model.MessageGroupStatus;
import com.example.chat.chat.model.MessageReaction;
import com.example.chat.chat.model.MessageStatus;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.repository.MessageGroupReactionRepository;
import com.example.chat.chat.repository.MessageGroupReceivedTimeRepository;
import com.example.chat.chat.repository.MessageGroupRepository;
import com.example.chat.chat.repository.MessageGroupStatusRepository;
import com.example.chat.mapper.MessageGroupMapper;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.model.LocalUser;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageGroupService {
        private final MessageGroupMapper messageGroupMapper;
        private final QueueService queueService;
        private final MessageGroupStatusRepository messageGroupStatusRepository;
        private final MessageGroupReceivedTimeRepository messageGroupReceivedTimeRepository;
        private final MessageGroupReactionRepository messageGroupReactionRepository;
        @Lazy
        private final GroupService groupService;
        private final LocalUserService localUserService;
        private final MessageGroupRepository messageGroupRepository;

        /***************************************************************************************************** */

        @Transactional
        public void sendMessage(SendMessageDto sendMessageDto) throws IOException, TimeoutException {
                Group group = groupService.getGroupById(sendMessageDto.getId());
                LocalUser user = localUserService.getLocalUserByToken();
                Queue queue = group.getQueue();
                MessageGroup parent = null;
                if (sendMessageDto.getParentMessageId() != null)
                        parent = getMessageById(sendMessageDto.getParentMessageId());

                MessageGroup messageGroup = new MessageGroup();
                messageGroup.setContent(sendMessageDto.getContent());
                messageGroup.setParentMessage(parent);
                messageGroup.setType(sendMessageDto.getType());
                messageGroup.setSendDateTime(LocalDateTime.now());
                messageGroup.setGroup(group);
                messageGroup.setSender(user);
                messageGroupRepository.save(messageGroup);
                List<LocalUser> members = groupService.getGroupMembers(sendMessageDto.getId());

                for (LocalUser member : members) {
                        if (!member.getId().equals(user.getId())) {
                                MessageGroupStatus messageGroupStatus = new MessageGroupStatus();
                                messageGroupStatus.setMessageGroup(messageGroup);
                                messageGroupStatus.setStatus(MessageStatus.SENT);
                                messageGroupStatus.setReceiver(member);
                                saveMessageGroupStatus(messageGroupStatus);
                        }
                }

                group.setLastUpdated(LocalDateTime.now());
                groupService.saveGroup(group);

                // MessageDto messageDto = MessageGroupMapper.INSTANCE.toDto(messageGroup);

                // messageDto.setId("group," + messageGroup.getId());

                queueService.sendMessage(queue,
                                messageGroupMapper.toDto(messageGroup), "Group");

        }

        public MessageGroup getMessageById(Integer id) {
                return messageGroupRepository.findById(id).orElse(null);
        }

        /**************************************************************************************************** */
        public void saveMessageGroupStatus(MessageGroupStatus messageGroupStatus) {
                messageGroupStatusRepository.save(messageGroupStatus);
        }

        /**************************************************************************************** */
        public MessageGroup saveMessageGroup(MessageGroup messageGroup) {
                return messageGroupRepository.save(messageGroup);
        }

        /**************************************************************************************** */

        public void setMessageGroupStatus(MessageGroup messageGroup, LocalUser receiver, MessageStatus received) {
                MessageGroupStatus messageGroupStatus = getMessageGroupStatusByReceiverId(messageGroup.getId(),
                                receiver.getId());

                if (messageGroupStatus == null) {
                        messageGroupStatus = new MessageGroupStatus();
                        messageGroupStatus.setMessageGroup(messageGroup);
                        messageGroupStatus.setReceiver(receiver);
                }
                messageGroupStatus.setStatus(received);
                messageGroupStatusRepository.save(messageGroupStatus);
        }

        /**************************************************************************************** */

        public void setReceiveDateTime(MessageGroup messageGroup, LocalUser receiver) {

                MessageGroupReceivedTime messageGroupReceivedTime = getMessageGroupReceivedTimeByMessageGroupIdAndReceiverId(
                                messageGroup.getId(), receiver.getId());
                if (messageGroupReceivedTime == null) {
                        messageGroupReceivedTime = new MessageGroupReceivedTime();
                        messageGroupReceivedTime.setReceiver(receiver);
                        messageGroupReceivedTime.setMessageGroup(messageGroup);
                }

                messageGroupReceivedTime.setTime(LocalDateTime.now());
                messageGroupReceivedTimeRepository.save(messageGroupReceivedTime);

        }

        /**************************************************************************************** */
        public MessageGroupReceivedTime getMessageGroupReceivedTimeByMessageGroupIdAndReceiverId(Integer messageGroupId,
                        Integer receiverId) {
                return messageGroupReceivedTimeRepository
                                .getMessageGroupReceivedTimeByMessageGroupIdAndReceiverId(messageGroupId, receiverId)
                                .orElse(null);
        }

        /**************************************************************************************** */

        public MessageGroupStatus getMessageGroupStatusByReceiverId(Integer messageGroupId, Integer receiverId) {
                return messageGroupStatusRepository.getMessageGroupStatusByReceiverId(messageGroupId, receiverId)
                                .orElse(null);
        }

        /**************************************************************************************** */
        public List<MessageGroupReaction> getMessageGroupReactionsByMessageGroupId(Integer messageGroupId) {
                return messageGroupRepository.getMessageGroupReactionsByMessageGroupId(messageGroupId);
        }

        /**************************************************************************************** */
        public Long getNumberOfUreadMessageByGroupId(Integer groupId) {
                return messageGroupStatusRepository.getNumberOfUnreadMessage(groupId);
        }

        /**************************************************************************************** */

        public MessageGroup getLastMessageByGroupId(Integer id) {
                List<MessageGroup> messageGroups = messageGroupRepository.getGroupMessages(id, PageRequest.of(0, 1));

                return messageGroups.isEmpty() ? null : messageGroups.get(0);
        }

        /**************************************************************************************** */

        public List<MessageGroup> getMessagesByGroupId(Integer groupId, Integer pageNumber) {
                List<MessageGroup> messageGroups = messageGroupRepository.getGroupMessages(groupId,
                                PageRequest.of(pageNumber, 20));

                return messageGroups;
        }

        /**************************************************************************************** */
        public void reactMessage(Integer messageId, MessageReaction reaction) {

                LocalUser localUser = localUserService.getLocalUserByToken();
                MessageGroupReaction messageGroupReaction = getReactionByMessageIdAndUserId(messageId,
                                localUser.getId());
                if (messageGroupReaction == null) {
                        messageGroupReaction = new MessageGroupReaction();
                        messageGroupReaction.setMessageGroup(getMessageById(messageId));
                        messageGroupReaction.setReactor(localUser);
                        messageGroupReaction.setReaction(reaction);

                }

                messageGroupReaction.setReaction(reaction);
                messageGroupReactionRepository.save(messageGroupReaction);

        }

        /**************************************************************************************** */
        private MessageGroupReaction getReactionByMessageIdAndUserId(Integer messageId, Integer id) {
                return messageGroupReactionRepository.getReactionByMessageIdAndUserId(messageId, id).orElse(null);
        }

        /**************************************************************************************** */

        public void unReactMessage(Integer messageId) {

                LocalUser user = localUserService.getLocalUserByToken();
                MessageGroupReaction messageGroupReaction = getReactionByMessageIdAndUserId(messageId, user.getId());
                if (messageGroupReaction != null) {
                        messageGroupReactionRepository.delete(messageGroupReaction);
                }
        }

        /**************************************************************************************** */
        public void deleteMessage(Integer messageId) {

                messageGroupRepository.deleteById(messageId);
        }

        /**************************************************************************************** */

        public void updateMessage(UpdateMessageDto updateMessage) {

                MessageGroup messageGroup = getMessageById(updateMessage.getMessageId());
                messageGroup.setContent(updateMessage.getContent());
                messageGroup.setType(updateMessage.getType());

                messageGroupRepository.save(messageGroup);
        }
}
