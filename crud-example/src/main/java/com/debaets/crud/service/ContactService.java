package com.debaets.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.debaets.crud.core.service.CrudServiceImpl;
import com.debaets.crud.model.entity.Contact;
import com.debaets.crud.repository.ContactRepository;

@Service
public class ContactService extends CrudServiceImpl<Contact, Long> {

	@Autowired
	public ContactService(ContactRepository contactRepository) {
		super(Contact.class, contactRepository);
	}
}
