package com.debaets.crud.repository;

import org.springframework.stereotype.Repository;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.model.entity.Contact;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {
}
