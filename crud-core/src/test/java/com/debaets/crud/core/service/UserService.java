package com.debaets.crud.core.service;

import com.debaets.crud.core.service.model.User;
import com.debaets.crud.core.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService extends CrudServiceImpl<User, Long> {

	@Autowired
	public UserService(UserRepository userRepository) {
		super(User.class, userRepository, new DictionaryService() {}, new ValidationService() {},
				new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}
}
