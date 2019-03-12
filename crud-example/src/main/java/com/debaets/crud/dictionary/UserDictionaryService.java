package com.debaets.crud.dictionary;

import org.springframework.stereotype.Service;

import com.debaets.crud.core.service.DictionaryService;

@Service
public class UserDictionaryService implements DictionaryService {

	@Override
	public String convertToEntityName(String argument) {
		return argument;
	}
}
