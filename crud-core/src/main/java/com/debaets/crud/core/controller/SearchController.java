package com.debaets.crud.core.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.List;

public interface SearchController <DTO, ID extends Serializable> extends Controller <DTO, ID>{

	@GetMapping(path = "/page")
	@ResponseStatus(HttpStatus.OK)
	default Page<DTO> search(@RequestParam("query") String query,
							 @RequestParam("page") int page,
							 @RequestParam("pageSize") int pageSize,
							 @RequestParam("sort") String sort){
		return getFacade().search(query, page, pageSize, sort);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	default List<DTO> search(@RequestParam("query") String query){
		return getFacade().search(query);
	}
}
