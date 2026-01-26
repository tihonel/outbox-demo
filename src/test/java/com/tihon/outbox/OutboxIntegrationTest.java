package com.tihon.outbox;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import com.tihon.outbox.containers.KafkaPostgresContainer;
import com.tihon.outbox.model.OutboxEntity;
import com.tihon.outbox.model.OutboxEntityPayload;
import com.tihon.outbox.model.OutboxEntityStatus;
import com.tihon.outbox.model.UserEntity;
import com.tihon.outbox.repository.OutboxEntityRepository;
import com.tihon.outbox.repository.UserRepository;
import com.tihon.outbox.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class OutboxIntegrationTest extends KafkaPostgresContainer {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OutboxEntityRepository outboxEntityRepository;

    private static final List<OutboxEntityPayload> events = new ArrayList<>();

    @KafkaListener(topics = "${kafka.updateTopic}", groupId = "outboxTest")
    void listen(OutboxEntityPayload payload) {
        log.info("input: {}", payload.toString());
        events.add(payload);
        log.info("events size: {}", events.size());
    }

    @Order(1)
    @Test
    public void testCreateUser() {
        //given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("gaga");

        //when
        UUID userCreatedId = userService.createUser(userEntity).getId();

        //then
        var userCreated = userRepository.findById(userCreatedId).get();

        assertEquals(userEntity, userCreated);
    }

    @Order(2)
    @Test
    public void testUpdateUser() {
        //given
        UserEntity existingUserEntity = userRepository.findAll(PageRequest.of(0, 1)).stream().findFirst().get();

        //when
        existingUserEntity.setUsername("abracadabra");
        userService.updateUser(existingUserEntity);

        //then
        await().atMost(1, TimeUnit.MINUTES).until(
                () -> !outboxEntityRepository.findAll().isEmpty()
        );

        OutboxEntity outboxEntity = outboxEntityRepository.findAll().stream().findFirst().get();

        log.info("wait fulling events");
        await().atMost(1, TimeUnit.MINUTES).until(
                () -> !events.isEmpty()
        );
        log.info("events not empty: {}", events.size());


        assertEquals(events.get(0).userId(), existingUserEntity.getId());
        assertEquals(events.get(0).changes().get("username"), existingUserEntity.getUsername());

        outboxEntity = outboxEntityRepository.findById(outboxEntity.getId()).get();

        assertEquals(OutboxEntityStatus.DONE, outboxEntity.getStatus());
    }
}
