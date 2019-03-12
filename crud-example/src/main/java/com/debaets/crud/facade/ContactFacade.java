package com.debaets.crud.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.debaets.crud.core.facade.CrudFacadeImpl;
import com.debaets.crud.core.service.CrudService;
import com.debaets.crud.model.dto.ContactDto;
import com.debaets.crud.model.entity.Contact;
import com.debaets.crud.service.ContactService;

@Service
public class ContactFacade extends CrudFacadeImpl<ContactDto, Contact, Long> {

	@Autowired
	private ContactService contactService;

	@Override
	public CrudService<Contact, Long> getCrudService() {
		return contactService;
	}
}
