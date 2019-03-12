package com.debaets.crud.converter.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.debaets.crud.mapper.ContactMapper;
import com.debaets.crud.model.dto.ContactDto;
import com.debaets.crud.model.entity.Contact;


@Service
public class ContactDtoMapper implements Converter<ContactDto, Contact> {

	@Autowired
	private ContactMapper mapper;

	@Override
	public Contact convert(ContactDto contactDto) {
		return mapper.map(contactDto);
	}
}
