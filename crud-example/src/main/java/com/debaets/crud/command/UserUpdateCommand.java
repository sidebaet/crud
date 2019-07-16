package com.debaets.crud.command;

import com.debaets.crud.core.annotation.command.CrudCreateCommand;
import com.debaets.crud.core.annotation.command.CrudUpdateCommand;
import com.debaets.crud.core.service.Command;
import com.debaets.crud.model.entity.User;

@CrudCreateCommand(entity = User.class)
@CrudUpdateCommand(entity = User.class)
public class UserUpdateCommand implements Command<User> {
    @Override
    public void execute(User entity) {

    }
}
