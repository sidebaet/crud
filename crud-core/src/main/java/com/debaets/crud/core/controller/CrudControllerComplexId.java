package com.debaets.crud.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

public interface CrudControllerComplexId <DTO, ID extends Serializable> extends Controller<DTO, ID> {

	@GetMapping(path = "/id")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	default DTO findOne(ID id){
		return getFacade().findOne(id);
	}

	@PostMapping(path = "/")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	default DTO create(@Validated @RequestBody DTO dto){
		return 	getFacade().create(dto);
	}

	@PutMapping(path = "/id")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	default DTO update(ID id, @Validated @RequestBody DTO dto){
		return getFacade().update(id, dto);
	}

	@DeleteMapping(path = "/id")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	default void delete(ID id){
		getFacade().delete(id);
	}


}
