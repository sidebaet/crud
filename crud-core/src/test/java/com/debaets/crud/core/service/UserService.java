package com.debaets.crud.core.service;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.core.service.model.User;
import com.debaets.crud.core.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudServiceImpl<User, Long> {

	@Autowired
	public UserService(UserRepository userRepository) {
		super(User.class, userRepository);
	}
}
