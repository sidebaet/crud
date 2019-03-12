package com.debaets.crud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.debaets.crud.core.controller.CrudController;
import com.debaets.crud.core.controller.SearchController;
import com.debaets.crud.core.facade.CrudFacade;
import com.debaets.crud.core.facade.CrudFacadeImpl;
import com.debaets.crud.model.dto.ContactDto;
import com.debaets.crud.model.entity.Contact;

@RestController
@RequestMapping("/api/contacts")
public class ContactController implements CrudController<ContactDto,Long>, SearchController<ContactDto, Long> {

	@com.debaets.crud.core.annotation.facade.CrudFacade(dto = ContactDto.class, entity = Contact.class)
	private CrudFacadeImpl<ContactDto, Contact, Long> contactFacade;

	@Override
	public CrudFacade<ContactDto, Long> getFacade() {
		return contactFacade;
	}
}
