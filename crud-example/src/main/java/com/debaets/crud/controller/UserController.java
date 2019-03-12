package com.debaets.crud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.debaets.crud.core.controller.CrudController;
import com.debaets.crud.core.controller.SearchController;
import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.core.facade.CrudFacadeImpl;
import com.debaets.crud.model.dto.UserDto;
import com.debaets.crud.model.entity.User;

@RestController
@RequestMapping("/api/users")
public class UserController implements CrudController<UserDto, Long>, SearchController<UserDto, Long> {

    @com.debaets.crud.core.annotation.facade.CrudFacade(dto = UserDto.class, entity = User.class)
    private CrudFacadeImpl<UserDto, User, Long> userFacade;

    @Override
    public CrudFacade<UserDto, Long> getFacade() {
        return userFacade;
    }
}
