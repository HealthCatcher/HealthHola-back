package com.example.hearurbackend.domain.user.service;

import com.example.hearurbackend.domain.user.dto.admin.SuspendUserDto;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserService userService;
    private final UserRepository userRepository;
    public void suspendUser(String adminUsername, SuspendUserDto suspendUserDto) {
        if (!userService.isUserAdmin(adminUsername)) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        User user = userService.getUser(suspendUserDto.getUsername()).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + suspendUserDto.getUsername()));
        user.suspendAccount(suspendUserDto.getDays());
        userRepository.save(user);
    }

    public void unsuspendUser(String username, SuspendUserDto suspendUserDto) {
        if (!userService.isUserAdmin(username)) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        User user = userService.getUser(suspendUserDto.getUsername()).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + suspendUserDto.getUsername()));
        user.unsuspendAccount();
        userRepository.save(user);
    }

    public void deleteUser(String username, SuspendUserDto suspendUserDto) {
        if (!userService.isUserAdmin(username)) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        User user = userService.getUser(suspendUserDto.getUsername()).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + suspendUserDto.getUsername()));
        userRepository.delete(user);
    }
}
