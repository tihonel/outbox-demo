package com.tihon.outbox.mapper;

import com.tihon.outbox.dto.UserDto;
import com.tihon.outbox.dto.UserDtoWithoutId;
import com.tihon.outbox.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto userToUserDto(UserEntity userEntity);

    UserEntity userDtoToUserEntity(UserDto userDto);

    @Mapping(target = "id", source = "id")
    UserEntity userDtoWithoutIdToUser(UserDtoWithoutId userDto, UUID id);
}
