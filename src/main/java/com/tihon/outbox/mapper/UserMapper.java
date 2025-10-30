package com.tihon.outbox.mapper;

import com.tihon.outbox.dto.UserDto;
import com.tihon.outbox.dto.UserDtoWithoutId;
import com.tihon.outbox.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto userToUserDto(User user);
    @Mapping(target = "id", source = "id")
    User userDtoWithoutIdToUser(UserDtoWithoutId userDto, Long id);
}
