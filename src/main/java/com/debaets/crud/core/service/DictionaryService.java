package com.debaets.crud.core.service;

public interface DictionaryService {

	default String convertToEntityName(String argument){
		return argument;
	}

}
