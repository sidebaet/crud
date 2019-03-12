package com.debaets.crud.mapper;

import org.mapstruct.Mapper;

import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User map(UserDto userDto);

    UserDto map(User user);
}
