package com.debaets.crud.core.controller;

import java.io.Serializable;

import com.debaets.crud.core.facade.CrudFacade;

public interface Controller <DTO, ID extends Serializable> {

	CrudFacade<DTO, ID> getFacade();

}
