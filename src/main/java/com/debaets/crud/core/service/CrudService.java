package com.debaets.crud.core.service;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;

import com.debaets.crud.core.repository.CrudRepository;

public interface CrudService <DTO, ENTITY, ID extends Serializable> {

	DTO findOne(ID id);

	/**
	 * Aggregate given dto with find ressource based on the aggregator service.
	 * @param id
	 * @param dto
	 * @return
	 */
	DTO aggrateById(ID id, DTO dto, AggregateService<DTO, ENTITY> aggregateService);

	List<DTO>  findByIds(List<ID> ids);

	DTO create(@Validated DTO dto);

	DTO update(ID id, @Validated DTO dto);

	void delete (DTO dto);

	void deleteById (ID id);

	Page<DTO> search(String searchQuery, PageRequest pageRequest);

	CrudRepository<ENTITY, ID> getRepository();

	default DictionaryService getDictionaryService(){
		return new DictionaryService() {};
	}

	List<DTO> search(@NotNull String searchQuery);
}
