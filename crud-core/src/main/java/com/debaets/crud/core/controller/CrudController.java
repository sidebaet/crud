package com.debaets.crud.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;


public interface CrudController <DTO, ID extends Serializable> extends Controller<DTO,ID>  {

	@GetMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	default DTO findOne(@PathVariable("id")ID id){
		return getFacade().findOne(id);
	}

	@PostMapping(path = "/")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	default DTO create(@Validated @RequestBody @Valid DTO dto){
		return 	getFacade().create(dto);
	}

	@PutMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	default DTO update(@PathVariable("id")ID id, @Validated @RequestBody  @Valid  DTO dto){
		return getFacade().update(id, dto);
	}

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	default void delete(@PathVariable("id")ID id){
		getFacade().delete(id);
	}



}
