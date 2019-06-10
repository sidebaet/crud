package com.debaets.crud.validator;

import com.debaets.crud.core.model.exception.ValidationException;
import com.debaets.crud.core.service.ValidationService;
import com.debaets.crud.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService implements ValidationService<User> {

    @Override
    public void validateForCreate(User entity) throws ValidationException {
        throw new ValidationException("field", "error code", "validation failed");
    }

    @Override
    public void validateForUpdate(User entity) throws ValidationException{

    }

    @Override
    public void validateForDelete(User entity) throws ValidationException{

    }
}
