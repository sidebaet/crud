package com.debaets.crud.command;

import com.debaets.crud.core.annotation.command.CrudCreateCommand;
import com.debaets.crud.core.annotation.command.CrudUpdateCommand;
import com.debaets.crud.core.service.Command;
import com.debaets.crud.model.entity.Contact;

@CrudCreateCommand(entity = Contact.class)
@CrudUpdateCommand(entity = Contact.class)
public class ContactUpdateCommand implements Command<Contact> {
    @Override
    public void execute(Contact entity) {

    }
}
