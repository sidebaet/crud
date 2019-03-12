package com.debaets.crud.core.controller;

import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.core.model.CrudEntity;

import java.io.Serializable;

public interface Controller <DTO, ID extends Serializable> {

	CrudFacade<DTO, ID> getFacade();

}
