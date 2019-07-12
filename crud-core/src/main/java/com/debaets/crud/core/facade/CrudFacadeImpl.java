package com.debaets.crud.core.facade;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.service.CrudService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class CrudFacadeImpl<DTO, ENTITY extends CrudEntity<ID>, ID extends Serializable>
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
	@Transactional
	public List<DTO> updateAll(@NotNull @Validated List<DTO> dtos) {
		if (CollectionUtils.isEmpty(dtos)){
			return Collections.EMPTY_LIST;
		}
		checkCastDtoToEntity();
		checkCastEntityToDto();
		var entities = dtos.stream()
							.map(dto -> convertToEntity(dto))
							.collect(Collectors.toList());
		return entities.stream()
					.map(entity -> crudService.update(entity.getId(), entity))
					.map(this::convertToDto)
					.collect(Collectors.toList());

	}

	@Override
	public void delete(@NotNull ID id) {
		crudService.delete(id);
	}

	@Override
	public Page<DTO> search(String query, int page, int pageSize, String sort, String direction) {
		checkCastEntityToDto();
		PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.fromString(direction), sort);

		Page<ENTITY> result;
		if (StringUtils.isEmpty(query)){
			result = crudService.findAll(pageRequest);
		}
		else {
			result = crudService.search(query, pageRequest);
		}
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
		if (StringUtils.isEmpty(searchQuery)){
			return (List<DTO>) conversionService.convert(crudService.findAll(),
					TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClassType)),
					TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(dtoClassType)));
		}
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
