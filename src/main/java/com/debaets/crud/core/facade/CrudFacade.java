package com.debaets.crud.core.facade;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.service.CrudService;

public interface CrudFacade <DTO, ID extends Serializable> {

	default DTO findOne(@NotNull ID id){
		return getCrudService().findOne(id);
	}

	default DTO create(@NotNull @Validated DTO dto){
		return getCrudService().create(dto);
	}

	default DTO update(@NotNull ID id, @NotNull @Validated DTO dto){
		return getCrudService().update(id, dto);
	}

	default void delete(@NotNull ID id){
		getCrudService().deleteById(id);
	}

	default Page<DTO> search(String query, int page, int pageSize){
		return getCrudService().search(query, new PageRequest(page, pageSize));
	}

	default List<DTO> search(@NotNull String searchQuery){
		return getCrudService().search(searchQuery);
	}

	CrudService<DTO, ? extends CrudEntity<ID>, ID> getCrudService();

}
