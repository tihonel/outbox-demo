package com.tihon.outbox.service;

import com.tihon.outbox.model.EventType;
import com.tihon.outbox.exception.CreateUserException;
import com.tihon.outbox.exception.UpdateUserException;
import com.tihon.outbox.model.OutboxEntityPayload;
import com.tihon.outbox.model.UserEntity;
import com.tihon.outbox.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        try {
            return userRepository.save(userEntity);
        } catch (Exception e) {
            log.error("Failed to create user {}", userEntity, e);
            throw new CreateUserException(e.getMessage(), e);
        }
    }

    @Transactional
    public UserEntity updateUser(UserEntity userEntity) {
        try {
            UserEntity updatedUserEntity = userRepository.save(userEntity);

            var payload = new OutboxEntityPayload(
                    updatedUserEntity.getId(), Map.of("username", updatedUserEntity.getUsername())
            );

            outboxService.saveNewMessage(payload, EventType.UPDATE_USER_DATA);
            return updatedUserEntity;
        } catch (Exception e){
            log.error("Failed to update user {}", userEntity, e);
            throw new UpdateUserException(e.getMessage(), e);
        }
    }
}
