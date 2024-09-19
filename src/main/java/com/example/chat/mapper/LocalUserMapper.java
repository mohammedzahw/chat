package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.registration.dto.LocalUserDto;
import com.example.chat.registration.dto.LocalUserProfileDto;
import com.example.chat.registration.dto.SignUpRequestDto;
import com.example.chat.registration.model.LocalUser;

@Component

public class LocalUserMapper {

    public LocalUser toEntity(LocalUserDto userDto) {
        if (userDto == null) {
            return null;
        }

        LocalUser.LocalUserBuilder localUser = LocalUser.builder();

        localUser.about(userDto.getAbout());
        localUser.active(userDto.getActive());
        localUser.id(userDto.getId());

        localUser.name(userDto.getName());

        return localUser.build();
    }

    /********************************************************************************************* */

    public LocalUser toEntity(SignUpRequestDto signUpRequestDto) {
        if (signUpRequestDto == null) {
            return null;
        }

        LocalUser.LocalUserBuilder localUser = LocalUser.builder();

        localUser.about(signUpRequestDto.getAbout());
        localUser.email(signUpRequestDto.getEmail());
        localUser.name(signUpRequestDto.getName());
        localUser.password(signUpRequestDto.getPassword());
        localUser.phone(signUpRequestDto.getPhone());

        return localUser.build();
    }

    /********************************************************************************************* */

    public LocalUserDto toDto(LocalUser user) {
        if (user == null) {
            return null;
        }

        LocalUserDto localUserDto = new LocalUserDto();

        localUserDto.setAbout(user.getAbout());
        localUserDto.setActive(user.getActive());
        localUserDto.setId(user.getId());
        if (user.getImageUser() != null)
            localUserDto.setImageUrl(user.getImageUser().getImageUrl());
        localUserDto.setName(user.getName());

        return localUserDto;
    }

    /********************************************************************************************* */

    public List<LocalUserDto> toDtoList(List<LocalUser> customers) {
        if (customers == null) {
            return null;
        }

        List<LocalUserDto> list = new ArrayList<LocalUserDto>(customers.size());
        for (LocalUser localUser : customers) {
            list.add(toDto(localUser));
        }

        return list;
    }

    /********************************************************************************************* */

    public List<LocalUser> toEntityList(List<LocalUserDto> customerDtos) {
        if (customerDtos == null) {
            return null;
        }
        /********************************************************************************************* */

        List<LocalUser> list = new ArrayList<LocalUser>(customerDtos.size());
        for (LocalUserDto localUserDto : customerDtos) {
            list.add(toEntity(localUserDto));
        }

        return list;
    }

    /********************************************************************************************* */

    public LocalUserProfileDto toUserProfileDto(LocalUser user) {
        if (user == null) {
            return null;
        }

        LocalUserProfileDto localUserProfileDto = new LocalUserProfileDto();

        localUserProfileDto.setAbout(user.getAbout());
        localUserProfileDto.setActive(user.getActive());
        localUserProfileDto.setEmail(user.getEmail());
        if (user.getImageUser() != null)
            localUserProfileDto.setImageUrl(user.getImageUser().getImageUrl());
        localUserProfileDto.setName(user.getName());
        localUserProfileDto.setPhone(user.getPhone());

        return localUserProfileDto;
    }
    /********************************************************************************************* */

}
