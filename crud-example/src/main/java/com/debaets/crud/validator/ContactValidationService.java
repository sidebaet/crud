package com.debaets.crud.validator;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.service.ValidationService;
import com.debaets.crud.model.entity.Contact;
import org.springframework.stereotype.Service;

@Service
public class ContactValidationService implements ValidationService<Contact> {

    @Override
    public void validateForCreate(CrudEntity entity) {

    }

    @Override
    public void validateForUpdate(CrudEntity entity) {

    }

    @Override
    public void validateForDelete(CrudEntity entity) {

    }
}
