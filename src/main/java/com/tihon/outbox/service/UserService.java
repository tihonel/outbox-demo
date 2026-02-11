package com.tihon.outbox.service;

import com.tihon.outbox.model.EventType;
import com.tihon.outbox.model.OutboxEntityPayload;
import com.tihon.outbox.model.UserEntity;
import com.tihon.outbox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Transactional
    public UserEntity updateUser(UserEntity userEntity) {
        UserEntity updatedUserEntity = userRepository.save(userEntity);

        var payload = new OutboxEntityPayload(
                updatedUserEntity.getId(), Map.of("username", updatedUserEntity.getUsername())
        );

        outboxService.saveNewMessage(payload, EventType.UPDATE_USER_DATA);
        return updatedUserEntity;
    }
}
