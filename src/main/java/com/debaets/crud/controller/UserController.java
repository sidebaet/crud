package com.debaets.crud.controller;

import com.debaets.crud.core.controller.CrudController;
import com.debaets.crud.core.controller.SearchController;
import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.facade.UserFacade;
import com.debaets.crud.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController implements CrudController<UserDto, Long>, SearchController<UserDto, Long> {

    @Autowired
    private UserFacade userFacade;

    @Override
    public CrudFacade<UserDto, Long> getFacade() {
        return userFacade;
    }
}
