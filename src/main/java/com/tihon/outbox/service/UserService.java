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
import java.util.Optional;

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
    public Optional<UserEntity> updateUser(UserEntity userEntity) {
        return userRepository.findById(userEntity.getId())
                .map(existingUser ->{
                            existingUser.setUsername(userEntity.getUsername());
                            userRepository.save(existingUser);

                            var payload = new OutboxEntityPayload(
                                    existingUser.getId(), Map.of("username", existingUser.getUsername())
                            );

                            outboxService.saveNewMessage(payload, EventType.UPDATE_USER_DATA);

                            return existingUser;
                        });
    }
}
