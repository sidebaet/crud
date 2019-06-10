package com.debaets.crud.validator;

import com.debaets.crud.core.model.exception.ValidationException;
import com.debaets.crud.core.service.ValidationService;
import com.debaets.crud.model.entity.Contact;
import org.springframework.stereotype.Service;

@Service
public class ContactValidationService implements ValidationService<Contact> {

    @Override
    public void validateForCreate(Contact entity) throws ValidationException {

    }

    @Override
    public void validateForUpdate(Contact entity) throws ValidationException  {

    }

    @Override
    public void validateForDelete(Contact entity) throws ValidationException  {

    }
}
