package com.debaets.crud.core.service;

import com.debaets.crud.core.model.CrudEntity;

public interface Command<ENTITY extends CrudEntity> {

    void execute(ENTITY entity);

}
