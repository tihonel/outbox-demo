package com.tihon.outbox.controller;

import com.tihon.outbox.dto.UserDto;
import com.tihon.outbox.dto.UserDtoWithoutId;
import com.tihon.outbox.mapper.UserMapper;
import com.tihon.outbox.model.UserEntity;
import com.tihon.outbox.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDtoWithoutId userDtoWithoutId) {
        var user = userService.createUser(userMapper.userDtoWithoutIdToUser(userDtoWithoutId, null));
        return ResponseEntity.ok(userMapper.userToUserDto(user));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto) {
        return userService.updateUser(userMapper.userDtoToUserEntity(userDto))
                .map(userMapper::userToUserDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
