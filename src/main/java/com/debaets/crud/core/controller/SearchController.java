package com.debaets.crud.core.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface SearchController <DTO, ID extends Serializable> extends Controller <DTO, ID>{

	@RequestMapping(path = "/page", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	default Page<DTO> search(@RequestParam("query") String query, @RequestParam("page") int page, @RequestParam("pageSize") int pageSize ){
		return getFacade().search(query, page, pageSize);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	default List<DTO> search(@RequestParam("query") String query){
		return getFacade().search(query);
	}
}
