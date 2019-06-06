package com.debaets.crud.validator;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.service.ValidationService;
import com.debaets.crud.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService implements ValidationService<User> {

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
