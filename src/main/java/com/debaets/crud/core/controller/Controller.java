package com.debaets.crud.core.controller;

import java.io.Serializable;

import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.core.model.CrudEntity;

public interface Controller <DTO, ID extends Serializable> {

	CrudFacade<DTO, ? extends CrudEntity<ID>, ID> getFacade();

}
