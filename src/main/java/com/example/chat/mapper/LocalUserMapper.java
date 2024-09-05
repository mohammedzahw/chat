package com.example.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.chat.registration.dto.LocalUserDto;
import com.example.chat.registration.dto.LocalUserProfileDto;
import com.example.chat.registration.dto.SignUpRequestDto;
import com.example.chat.registration.model.LocalUser;

@Mapper
public interface LocalUserMapper {
    LocalUserMapper INSTANCE = Mappers.getMapper(LocalUserMapper.class);

    LocalUser toEntity(LocalUserDto userDto);

    LocalUser toEntity(SignUpRequestDto signUpRequestDto);

    LocalUserDto toDto(LocalUser user);

    List<LocalUserDto> toDtoList(List<LocalUser> customers);

    List<LocalUser> toEntityList(List<LocalUserDto> customerDtos);

    LocalUserProfileDto toUserProfileDto(LocalUser user);
}