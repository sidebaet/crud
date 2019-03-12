package com.debaets.crud.service;

import org.springframework.stereotype.Service;

import com.debaets.crud.core.service.CrudServiceImpl;
import com.debaets.crud.model.entity.Contact;

@Service
public class ContactService extends CrudServiceImpl<Contact, Long> {
}
