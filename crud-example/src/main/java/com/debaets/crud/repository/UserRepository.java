package com.debaets.crud.repository;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.model.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
