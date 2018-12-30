package com.debaets.crud.core.service.repository;

import org.springframework.stereotype.Repository;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.core.service.model.User;

@Repository("testUserRepo")
public interface UserRepository extends CrudRepository<User, Long> {
}
