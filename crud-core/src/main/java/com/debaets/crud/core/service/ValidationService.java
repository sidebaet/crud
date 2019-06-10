package com.debaets.crud.core.service;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.model.exception.ValidationException;

public interface ValidationService<ENTITY extends CrudEntity> {
    default void validateForCreate(ENTITY entity) throws ValidationException {}

    default void validateForUpdate(ENTITY entity) throws ValidationException {}

    default void validateForDelete(ENTITY entity) throws ValidationException {}
}
