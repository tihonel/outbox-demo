package com.tihon.outbox.controller;

import com.tihon.outbox.dto.UserDtoWithoutId;
import com.tihon.outbox.mapper.UserMapper;
import com.tihon.outbox.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
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

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @RequestBody UserDtoWithoutId userDtoWithoutId) {
        var user = userService.updateUser(userMapper.userDtoWithoutIdToUser(userDtoWithoutId, id));
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }
}
