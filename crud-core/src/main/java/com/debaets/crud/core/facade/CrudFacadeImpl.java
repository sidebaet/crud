package com.debaets.crud.core.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.debaets.crud.core.service.CrudService;

@SuppressWarnings("ALL")
public class CrudFacadeImpl<DTO, ENTITY, ID extends Serializable>
		implements CrudFacade<DTO, ID> {

	@Autowired
	private ConversionService conversionService;
	private CrudService<ENTITY,ID> crudService;

	private Class<DTO> dtoClassType;

	private Class<ENTITY> entityClassType;


	public CrudFacadeImpl(){
		dtoClassType = (Class<DTO>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		entityClassType = (Class<ENTITY>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
	}

	public CrudFacadeImpl(Class<DTO> dtoClassType,
				Class<ENTITY> entityClassType,
				ConversionService conversionService,
				CrudService<ENTITY,ID> crudService
			){
		this.dtoClassType = dtoClassType;
		this.entityClassType = entityClassType;
		this.conversionService = conversionService;
		this.crudService = crudService;
	}

	@PostConstruct
	public void postConstruct() {
		assert (crudService != null);
		assert (conversionService != null);
	}

	@Override
	public DTO findOne(@NotNull ID id) {
		checkCastEntityToDto();
		return convertToDto(crudService.findOne(id));
	}

	@Override
	public List<DTO> findByIds(List<ID> ids) {
		checkCastEntityToDto();
		return (List<DTO>) conversionService.convert(crudService.findByIds(ids),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClassType)),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(dtoClassType)));
	}

	@Override
	public DTO create(@NotNull @Validated DTO dto) {
		checkCastEntityToDto();
		checkCastDtoToEntity();
		return convertToDto(crudService.create(convertToEntity(dto)));
	}

	@Override
	public DTO update(@NotNull ID id, @NotNull @Validated DTO dto) {
		checkCastDtoToEntity();
		checkCastEntityToDto();
		return convertToDto(crudService.update(id, convertToEntity(dto)));
	}

	@Override
	public void delete(@NotNull ID id) {
		crudService.delete(id);
	}

	@Override
	public Page<DTO> search(String query, int page, int pageSize) {
		checkCastEntityToDto();
		PageRequest pageRequest = new PageRequest(page, pageSize);
		Page<ENTITY> result = crudService.search(query, pageRequest);
		List<ENTITY> content = result.getContent();

		if (result.isEmpty()){
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}

		List<DTO> dtos = (List<DTO>) conversionService.convert(content,
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClassType)),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(dtoClassType)));
		return new PageImpl<>(dtos,pageRequest,result.getTotalElements());
	}

	@Override
	public List<DTO> search(@NotNull String searchQuery) {
		checkCastEntityToDto();
		return (List<DTO>) conversionService.convert(crudService.search(searchQuery),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClassType)),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(dtoClassType)));
	}
	
	protected void checkCastEntityToDto() {
		if (!conversionService.canConvert(entityClassType, dtoClassType)) {
			throw new ConverterNotFoundException(TypeDescriptor.valueOf(entityClassType),
					TypeDescriptor.valueOf(dtoClassType));
		}
	}

	protected void checkCastDtoToEntity() {
		if (!conversionService.canConvert(dtoClassType,entityClassType)) {
			throw new ConverterNotFoundException(TypeDescriptor.valueOf(dtoClassType),
					TypeDescriptor.valueOf(entityClassType));
		}
	}

	protected DTO convertToDto(ENTITY entity) {
		return conversionService.convert(entity, dtoClassType);
	}

	protected ENTITY convertToEntity(DTO dto) {
		return conversionService.convert(dto, entityClassType);
	}

}
