package com.debaets.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.debaets.crud.core.controller.CrudController;
import com.debaets.crud.core.controller.SearchController;
import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.facade.ContactFacade;
import com.debaets.crud.model.dto.ContactDto;


@RestController
@RequestMapping("/api/contacts")
public class ContactController implements CrudController<ContactDto,Long>, SearchController<ContactDto, Long> {

	@Autowired
	private ContactFacade contactFacade;

	@Override
	public CrudFacade<ContactDto, ? extends CrudEntity<Long>, Long> getFacade() {
		return contactFacade;
	}
}
