package com.debaets.crud.core.facade;

import com.debaets.crud.core.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
