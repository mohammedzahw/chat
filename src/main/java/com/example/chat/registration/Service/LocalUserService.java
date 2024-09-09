package com.example.chat.registration.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.chat.exception.CustomException;
import com.example.chat.mapper.LocalUserMapper;
import com.example.chat.registration.dto.LocalUserDto;
import com.example.chat.registration.dto.LocalUserProfileDto;
import com.example.chat.registration.dto.UpdateUserRequestDto;
import com.example.chat.registration.model.LocalUser;
import com.example.chat.registration.repository.LocalUserRepository;
import com.example.chat.security.TokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalUserService {
    private final LocalUserMapper localUserMapper;
    private final LocalUserRepository localUserRepository;

    private final TokenUtil tokenUtil;


    /*******************************************************************************************/

    public LocalUserDto getUser() {

        LocalUser user = localUserRepository.findById(tokenUtil.getUserId()).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return localUserMapper.toDto(user);

    }

    /************************************************************************************************** */
    public Integer getUserId() {

        return tokenUtil.getUserId();
    }

    /*******************************************************************************************/
    public LocalUser getLocalUserById(Integer Id) {
        LocalUser user = localUserRepository.findById(Id).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return user;
    }

    /*******************************************************************************************/
    public LocalUser getLocalUserByToken() {

        return getLocalUserById(tokenUtil.getUserId());

    }

    /*******************************************************************************************/
    public LocalUser getLocalUserByName(String name) {
        LocalUser user = localUserRepository.findByEmail(name).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return user;
    }

    /*******************************************************************************************/
    public LocalUser getLocalUserByEmail(String email) {
        LocalUser user = localUserRepository.findByEmail(email).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return user;
    }

    /*******************************************************************************************/
    public LocalUserProfileDto getUserProfile() {
        LocalUser user = localUserRepository.findById(tokenUtil.getUserId()).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        return localUserMapper.toUserProfileDto(user);
    }

    /*******************************************************************************************/

    public void deleteUser() {

        LocalUser user = localUserRepository.findById(tokenUtil.getUserId()).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        localUserRepository.delete(user);

    }

    /*******************************************************************************************/
    public LocalUserDto updateUser(UpdateUserRequestDto updateUserRequest) {
        LocalUser user = localUserRepository.findById(tokenUtil.getUserId()).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        user.setName(updateUserRequest.getName());
        user.setEmail(updateUserRequest.getEmail());
        localUserRepository.save(user);
        return localUserMapper.toDto(user);
    }

    /*******************************************************************************************/

    void saveUser(LocalUser user) {
        localUserRepository.save(user);
    }

    /*******************************************************************************************/

    public List<String> getQueuesByUserId() {

        return localUserRepository.getQueuesByUserId(tokenUtil.getUserId()).stream().map(
                q -> q.getName())
                .toList();

    }

    /*******************************************************************************************/

    public void addQueue(Integer userId, Integer queueID) {
        localUserRepository.addQueue(userId, queueID);
    }

    /*******************************************************************************************/
    public void addChat(Integer userId, Integer id) {
        localUserRepository.addChat(userId, id);
    }

}
