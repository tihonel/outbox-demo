package com.tihon.outbox.service;

import com.tihon.outbox.events.EventType;
import com.tihon.outbox.events.UpdateUserEvent;
import com.tihon.outbox.exception.CreateUserException;
import com.tihon.outbox.exception.UpdateUserException;
import com.tihon.outbox.mapper.UserMapper;
import com.tihon.outbox.model.User;
import com.tihon.outbox.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OutboxService outboxService;

    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to create user {}", user, e);
            throw new CreateUserException(e.getMessage(), e);
        }
    }

    public User updateUser(User user) {
        try {
            User updatedUser = userRepository.save(user);
            outboxService.saveNewMessage(updatedUser, EventType.UPDATE);
            return updatedUser;
        } catch (Exception e){
            log.error("Failed to update user {}", user, e);
            throw new UpdateUserException(e.getMessage(), e);
        }
    }
}
