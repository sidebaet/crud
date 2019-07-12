package com.debaets.crud.core.facade;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public interface CrudFacade <DTO, ID extends Serializable> {

	DTO findOne(@NotNull ID id);

	List<DTO> findByIds(List<ID> ids);

	DTO create(@NotNull @Validated DTO dto);

	DTO update(@NotNull ID id, @NotNull @Validated DTO dto);

	List<DTO> updateAll(@NotNull @Validated List<DTO> dtos);

	void delete(@NotNull ID id);

	Page<DTO> search(@NotNull String query, int page, int pageSize, String sort);

	List<DTO> search(@NotNull String searchQuery);

}
