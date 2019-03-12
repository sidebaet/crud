package com.debaets.crud.converter.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.debaets.crud.mapper.ContactMapper;
import com.debaets.crud.model.dto.ContactDto;
import com.debaets.crud.model.entity.Contact;

@Service
public class ContactConverter implements Converter<Contact, ContactDto> {

	@Autowired
	private ContactMapper mapper;

	@Override
	public ContactDto convert(Contact contact) {
		return mapper.map(contact);
	}
}
