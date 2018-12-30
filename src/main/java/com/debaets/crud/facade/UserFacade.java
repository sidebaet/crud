package com.debaets.crud.facade;

import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.service.CrudService;
import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class UserFacade implements CrudFacade <UserDto, Long> {

    @Autowired
    private UserService userService;

    @Override
    public CrudService<UserDto, ? extends CrudEntity<Long>, Long> getCrudService() {
        return userService;
    }
}
