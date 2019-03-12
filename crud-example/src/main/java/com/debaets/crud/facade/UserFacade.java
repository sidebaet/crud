package com.debaets.crud.facade;

import com.debaets.crud.core.facade.CrudFacadeImpl;
import com.debaets.crud.core.service.CrudService;
import com.debaets.crud.core.service.CrudServiceImpl;
import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.model.entity.User;
import org.springframework.stereotype.Service;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class UserFacade extends CrudFacadeImpl<UserDto, User, Long> {

    @com.debaets.crud.core.annotation.CrudService(entity = User.class)
    private CrudServiceImpl<User, Long> userService;

    @Override
    public CrudService<User, Long> getCrudService() {
        return userService;
    }
}
