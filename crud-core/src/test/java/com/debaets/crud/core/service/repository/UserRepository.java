package com.debaets.crud.core.service.repository;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.core.service.model.User;
import org.springframework.stereotype.Repository;

@Repository("testUserRepo")
public interface UserRepository extends CrudRepository<User, Long> {
}
