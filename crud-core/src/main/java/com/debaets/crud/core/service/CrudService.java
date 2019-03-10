package com.debaets.crud.core.service;

import com.debaets.crud.core.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public interface CrudService <ENTITY, ID extends Serializable> {

	ENTITY findOne(ID id);

	List<ENTITY>  findByIds(List<ID> ids);

	ENTITY create(@Validated ENTITY dto);

	ENTITY update(ID id, @Validated ENTITY dto);

	void delete (ID id);

	void deleteById (ID id);

	Page<ENTITY> search(String searchQuery, PageRequest pageRequest);

	CrudRepository<ENTITY, ID> getRepository();

	default DictionaryService getDictionaryService(){
		return new DictionaryService() {};
	}

	List<ENTITY> search(@NotNull String searchQuery);
}
