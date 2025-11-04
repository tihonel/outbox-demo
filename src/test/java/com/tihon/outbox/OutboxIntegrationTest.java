package com.tihon.outbox;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import com.tihon.outbox.containers.KafkaPostgresContainer;
import com.tihon.outbox.model.OutboxEntry;
import com.tihon.outbox.model.OutboxEntryPayload;
import com.tihon.outbox.model.OutboxEntryStatus;
import com.tihon.outbox.model.User;
import com.tihon.outbox.repository.OutboxEntryRepository;
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
    OutboxEntryRepository outboxEntryRepository;

    private static final List<OutboxEntryPayload> events = new ArrayList<>();

    @KafkaListener(topics = "${kafka.updateTopic}", groupId = "outboxTest")
    void listen(OutboxEntryPayload payload) {
        log.info("input: {}", payload.toString());
        events.add(payload);
        log.info("events size: {}", events.size());
    }

    @Order(1)
    @Test
    public void testCreateUser() {
        //given
        User user = new User();
        user.setUsername("gaga");

        //when
        Long userCreatedId = userService.createUser(user).getId();

        //then
        var userCreated = userRepository.findById(userCreatedId).get();

        assertEquals(user, userCreated);
    }

    @Order(2)
    @Test
    public void testUpdateUser() {
        //given
        User existingUser = userRepository.findAll(PageRequest.of(0, 1)).stream().findFirst().get();

        //when
        existingUser.setUsername("abracadabra");
        userService.updateUser(existingUser);

        //then
        await().atMost(1, TimeUnit.MINUTES).until(
                () -> !outboxEntryRepository.findAll().isEmpty()
        );

        OutboxEntry outboxEntry = outboxEntryRepository.findAll().stream().findFirst().get();

        log.info("wait fulling events");
        await().atMost(1, TimeUnit.MINUTES).until(
                () -> !events.isEmpty()
        );
        log.info("events not empty: {}", events.size());


        assertEquals(events.get(0).userId(), existingUser.getId());
        assertEquals(events.get(0).changes().get("username"), existingUser.getUsername());

        outboxEntry = outboxEntryRepository.findById(outboxEntry.getId()).get();

        assertEquals(OutboxEntryStatus.DONE, outboxEntry.getStatus());
    }
}
