package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.UserRequestDto;
import com.darshan.eams.dto.response.UserResponseDto;
import com.darshan.eams.entity.Role;
import com.darshan.eams.entity.User;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.DuplicateResourceException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.UserMapper;
import com.darshan.eams.repository.RoleRepository;
import com.darshan.eams.repository.UserRepository;
import com.darshan.eams.service.interfaces.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger log = LoggerFactory.getLogger(UserManagementServiceImpl.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        if (userRepository.existsByUsernameIgnoreCase(requestDto.getUsername()))
            throw new DuplicateResourceException("User", "username", requestDto.getUsername());

        if (userRepository.existsByEmailIgnoreCase(requestDto.getEmail()))
            throw new DuplicateResourceException("User", "email", requestDto.getEmail());

        if (requestDto.getPassword() == null || requestDto.getPassword().isBlank())
            throw new BusinessRuleViolationException("Password is required when creating a new user");


        Role role = roleRepository.findByRoleName(requestDto.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", requestDto.getRoleName()));

        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(role);
        user.setEnabled(requestDto.isEnabled());
        user.setAccountNonLocked(true);

        User saved = userRepository.save(user);
        log.info("User created: id={}, username={}, role={}", saved.getId(), saved.getUsername(), role.getRoleName());
        return userMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        User user = findUserOrThrow(id);

        if (!user.getUsername().equalsIgnoreCase(requestDto.getUsername())
                && userRepository.existsByUsernameIgnoreCase(requestDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", requestDto.getUsername());
        }

        if (!user.getEmail().equalsIgnoreCase(requestDto.getEmail())
                && userRepository.existsByEmailIgnoreCase(requestDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", requestDto.getEmail());
        }

        guardAgainstSelfLockout(user, requestDto.isEnabled());

        Role role = roleRepository.findByRoleName(requestDto.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", requestDto.getRoleName()));

        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setRole(role);
        user.setEnabled(requestDto.isEnabled());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));


        User updated = userRepository.save(user);
        log.info("User updated: id={}", updated.getId());
        return userMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        return userMapper.toResponseDto(findUserOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void toggleEnabled(Long id) {
        User user = findUserOrThrow(id);
        guardAgainstSelfLockout(user, !user.isEnabled());
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        log.info("User {} {}", id, user.isEnabled() ? "enabled" : "disabled");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findUserOrThrow(id);
        guardAgainstSelfLockout(user, false);
        userRepository.delete(user);
        log.info("User deleted: id={}", id);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private void guardAgainstSelfLockout(User targetUser, boolean willBeEnabled) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (targetUser.getUsername().equalsIgnoreCase(currentUsername) && !willBeEnabled) {
            throw new BusinessRuleViolationException("You cannot disable or delete your own account");
        }
    }
}