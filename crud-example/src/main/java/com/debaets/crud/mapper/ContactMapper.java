package com.debaets.crud.mapper;

import org.mapstruct.Mapper;

import com.debaets.crud.model.dto.ContactDto;
import com.debaets.crud.model.entity.Contact;

@Mapper(componentModel = "spring")
public interface ContactMapper {

	ContactDto map(Contact contact);

	Contact map(ContactDto contactDto);

}
