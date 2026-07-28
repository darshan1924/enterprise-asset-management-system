package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.UserRequestDto;
import com.darshan.eams.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {

    UserResponseDto create(UserRequestDto requestDto);

    UserResponseDto update(Long id, UserRequestDto requestDto);

    UserResponseDto getById(Long id);

    Page<UserResponseDto> getAll(Pageable pageable);

    void toggleEnabled(Long id);

    void delete(Long id);
}