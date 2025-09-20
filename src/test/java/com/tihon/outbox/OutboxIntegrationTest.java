package com.tihon.outbox;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tihon.outbox.containers.KafkaPostgresContainer;
import com.tihon.outbox.events.UpdateUserEvent;
import com.tihon.outbox.model.OutboxEntry;
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
    @Autowired
    ObjectMapper objectMapper;

    private static final List<UpdateUserEvent> events = new ArrayList<>();

    @KafkaListener(topics = "${kafka.updateTopic}", groupId = "outboxTest")
    void listen(String eventString) throws JsonProcessingException {
        log.info("input: {}", eventString);
        UpdateUserEvent event = objectMapper.readValue(eventString, UpdateUserEvent.class);
        log.info("event: {}", event);
        events.add(event);
        log.info("events size: {}", events.size());
    }

    @Order(1)
    @Test
    public void testCreateUser() throws Exception {
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
    public void testUpdateUser() throws Exception {
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

        assertEquals(OutboxEntryStatus.IN_PROGRESS, outboxEntry.getStatus());

        log.info("wait fulling events");
        await().atMost(1, TimeUnit.MINUTES).until(
                () -> !events.isEmpty()
        );
        log.info("events not empty: {}", events.size());


        assertEquals(events.get(0).getUpdatedUser(), existingUser);

        outboxEntry = outboxEntryRepository.findById(outboxEntry.getId()).get();

        assertEquals(OutboxEntryStatus.COMPLETED, outboxEntry.getStatus());
    }
}
