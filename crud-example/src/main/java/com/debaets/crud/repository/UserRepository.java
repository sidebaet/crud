package com.debaets.crud.repository;

import org.springframework.stereotype.Repository;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.model.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
