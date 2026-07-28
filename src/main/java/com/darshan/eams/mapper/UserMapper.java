package com.darshan.eams.mapper;

import com.darshan.eams.dto.response.UserResponseDto;
import com.darshan.eams.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        if (user == null)
            return null;

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRole().getRoleName())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public List<UserResponseDto> toResponseDtoList(List<User> users) {
        return users.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}