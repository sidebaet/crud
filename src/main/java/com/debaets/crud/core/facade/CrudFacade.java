package com.debaets.crud.core.facade;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.service.CrudService;

public interface CrudFacade <DTO, ENTITY, ID extends Serializable> {

	DTO findOne(@NotNull ID id);

	List<DTO> findByIds(List<ID> ids);

	DTO create(@NotNull @Validated DTO dto);

	DTO update(@NotNull ID id, @NotNull @Validated DTO dto);

	void delete(@NotNull ID id);

	Page<DTO> search(String query, int page, int pageSize);

	List<DTO> search(@NotNull String searchQuery);

	CrudService<ENTITY, ID> getCrudService();

}
