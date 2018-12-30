package com.debaets.crud.core.controller;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface CrudControllerComplexId <DTO, ID extends Serializable> extends Controller<DTO, ID> {

	@RequestMapping(path = "/id", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	default DTO findOne(ID id){
		return getFacade().findOne(id);
	}

	@RequestMapping(path = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	default DTO create(@Validated @RequestBody DTO dto){
		return 	getFacade().create(dto);
	}

	@RequestMapping(path = "/id", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	default DTO update(ID id, @Validated @RequestBody DTO dto){
		return getFacade().update(id, dto);
	}

	@RequestMapping(path = "/id", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	default void delete(ID id){
		getFacade().delete(id);
	}


}
