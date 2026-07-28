package com.darshan.eams.dto.response;

import com.darshan.eams.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String username;

    private String email;

    private UserRole roleName;

    private boolean enabled;

    private boolean accountNonLocked;

    private LocalDateTime createdAt;
}