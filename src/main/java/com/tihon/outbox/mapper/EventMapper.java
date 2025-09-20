package com.tihon.outbox.mapper;

import static com.tihon.outbox.events.EventType.UPDATE;

import com.tihon.outbox.events.Event;
import com.tihon.outbox.events.EventType;
import com.tihon.outbox.events.UpdateUserEvent;
import com.tihon.outbox.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    default Event toEvent(User user, EventType eventtype) {
        return switch (eventtype) {
            case UPDATE -> toUpdateUserEvent(user);
            case CREAT -> throw new RuntimeException("Method not implemented yet");
        };
    }


    @Mapping(source = "user", target = "updatedUser")
    UpdateUserEvent toUpdateUserEvent (User user);
}
