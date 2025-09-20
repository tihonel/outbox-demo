package com.tihon.outbox.events;

import com.tihon.outbox.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserEvent extends Event {
    private User updatedUser;
}
