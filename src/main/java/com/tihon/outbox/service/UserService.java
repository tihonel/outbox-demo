package com.tihon.outbox.service;

import com.tihon.outbox.model.EventType;
import com.tihon.outbox.exception.CreateUserException;
import com.tihon.outbox.exception.UpdateUserException;
import com.tihon.outbox.model.OutboxEntryPayload;
import com.tihon.outbox.model.User;
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
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to create user {}", user, e);
            throw new CreateUserException(e.getMessage(), e);
        }
    }

    @Transactional
    public User updateUser(User user) {
        try {
            User updatedUser = userRepository.save(user);

            var payload = new OutboxEntryPayload(
                    updatedUser.getId(), Map.of("username", updatedUser.getUsername())
            );

            outboxService.saveNewMessage(payload, EventType.UPDATE_USER_DATA);
            return updatedUser;
        } catch (Exception e){
            log.error("Failed to update user {}", user, e);
            throw new UpdateUserException(e.getMessage(), e);
        }
    }
}
