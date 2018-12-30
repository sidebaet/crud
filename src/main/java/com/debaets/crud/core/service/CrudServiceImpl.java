package com.debaets.crud.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import com.debaets.crud.core.model.exception.EntityAlreadyExistsException;
import com.debaets.crud.core.model.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.model.Operators;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "unchecked"})
public abstract class CrudServiceImpl<DTO, ENTITY extends CrudEntity<ID>, ID extends Serializable> implements CrudService<DTO, ENTITY, ID> {

	@Autowired
	private ConversionService conversionService;

	private Class<DTO> dtoClassType;

	private Class<ENTITY> entityClassType;

	public CrudServiceImpl(){
		dtoClassType = (Class<DTO>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		entityClassType = (Class<ENTITY>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@PostConstruct
	public void postConstruct() {
		assert (getRepository() != null);
	}

	@Override
	public DTO findOne(@NotNull ID id) {
		checkCastEntityToDto();
		return getRepository().findById(id)
				.map(this::convertToDto)
				.orElseThrow(() -> new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+id+" not found"));
	}

	@Override
	public DTO aggrateById(@NotNull ID id, @NotNull DTO dto, @NotNull  AggregateService<DTO, ENTITY> aggregateService) {
		return getRepository().findById(id)
				.map(entity -> aggregateService.aggregate(dto, entity))
				.orElseThrow(() -> new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+id+" not found"));
	}

	@Override
	public List<DTO> findByIds(List<ID> ids) {
		if (CollectionUtils.isEmpty(ids)){
			return Collections.emptyList();
		}
		checkCastEntityToDto();
		List<ENTITY> entities = getRepository().findAll();
		entities.removeAll(Collections.singleton(null));
		return (List<DTO>) conversionService.convert(entities,
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClassType)),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(dtoClassType)));
	}

	@Override
	public DTO create(@Validated @NotNull DTO dto) {
		checkCastDtoToEntity();
		checkCastEntityToDto();
		ENTITY entity = convertToEntity(dto);
		if (entity.getId() != null && getRepository().existsById(entity.getId())) {
			throw new EntityAlreadyExistsException("Entity with id : " + entity.getId().toString() + ", already exists");
		}
		return convertToDto(getRepository().save(entity));
	}

	@Override
	public DTO update(@NotNull ID id, @Validated @NotNull DTO dto) {
		checkCastDtoToEntity();
		checkCastEntityToDto();
		if (!getRepository().existsById(id)){
			throw new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+id+" not found");
		}
		ENTITY entityToUpdate = convertToEntity(dto);
		entityToUpdate.setId(id);
		return convertToDto(getRepository().save(entityToUpdate));
	}

	@Override
	public void delete(@NotNull DTO dto) {
		checkCastDtoToEntity();
		ENTITY entity = convertToEntity(dto);
		ID id = entity.getId();
		if (!getRepository().existsById(entity.getId())){
			throw new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+ id +" not found");
		}
		getRepository().deleteById(id);
	}

	@Override
	public void deleteById(@NotNull ID id) {
		if (!getRepository().existsById(id)){
			throw new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+ id +" not found");
		}
		getRepository().deleteById(id);
	}

	@Override
	public Page<DTO> search(@NotNull String searchQuery, PageRequest pageRequest) {
		checkCastEntityToDto();
		Node rootNode = new RSQLParser(Operators.getOperators()).parse(searchQuery);
		Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>(getDictionaryService(), false));
		Page<ENTITY> page = getRepository().findAll(spec, pageRequest);
		List<ENTITY> content = page.getContent();

		if (CollectionUtils.isEmpty(content)){
			return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
		}

		List<DTO> dtos = (List<DTO>) conversionService.convert(content,
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(entityClassType)),
				TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(dtoClassType)));
		return new PageImpl<>(dtos,pageRequest,page.getTotalElements());
	}

	@Override
	public List<DTO> search(@NotNull String searchQuery) {
		checkCastEntityToDto();
		Node rootNode = new RSQLParser(Operators.getOperators()).parse(searchQuery);
		Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>(getDictionaryService(), true));
		List<ENTITY> content = getRepository().findAll(spec);
		content.removeAll(Collections.singleton(null));

		return (List<DTO>) conversionService.convert(content,
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
