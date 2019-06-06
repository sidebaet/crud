package com.debaets.crud.core.service;

import com.debaets.crud.core.model.CrudEntity;

public interface ValidationService<ENTITY extends CrudEntity> {
    default void validateForCreate(CrudEntity entity) {}

    default void validateForUpdate(CrudEntity entity) {}

    default void validateForDelete(CrudEntity entity) {}
}
