package com.debaets.crud.converter.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.debaets.crud.mapper.UserMapper;
import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.model.entity.User;


@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class UserConverter implements Converter<User, UserDto> {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto convert(User user) {
        return userMapper.map(user);
    }
}
