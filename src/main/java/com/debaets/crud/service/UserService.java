package com.debaets.crud.service;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.core.service.CrudServiceImpl;
import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.model.entity.User;
import com.debaets.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service("CrudUserService")
public class UserService extends CrudServiceImpl<UserDto, User, Long> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CrudRepository<User, Long> getRepository() {
        return userRepository;
    }
}
