package com.tihon.outbox.controller;

import com.tihon.outbox.dto.UserDto;
import com.tihon.outbox.dto.UserDtoWithoutId;
import com.tihon.outbox.mapper.UserMapper;
import com.tihon.outbox.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDtoWithoutId userDtoWithoutId) {
        var user = userService.createUser(userMapper.userDtoWithoutIdToUser(userDtoWithoutId, null));
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
        var user = userService.updateUser(userMapper.userDtoToUserEntity(userDto));
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }
}
