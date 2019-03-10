package com.debaets.crud.mapper;

import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User map(UserDto userDto);

    UserDto map(User user);
}
