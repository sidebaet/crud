package com.debaets.crud.core.service;

import com.debaets.crud.core.model.CrudEntity;
import com.debaets.crud.core.model.Operators;
import com.debaets.crud.core.model.exception.EntityAlreadyExistsException;
import com.debaets.crud.core.model.exception.ResourceNotFoundException;
import com.debaets.crud.core.repository.CrudRepository;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"SpringJavaAutowiredMembersInspection", "unchecked"})
public class CrudServiceImpl<ENTITY extends CrudEntity<ID>, ID extends Serializable>
		implements CrudService<ENTITY, ID> {


	private Class<ENTITY> entityClassType;

	private final CrudRepository<ENTITY, ID> crudRepository;
	private final DictionaryService dictionaryService;

	public CrudServiceImpl(Class<ENTITY> entityClassType,
			CrudRepository<ENTITY,ID> crudRepository,
			DictionaryService dictionaryService){
		this.entityClassType = entityClassType;
		this.crudRepository = crudRepository;
		this.dictionaryService = dictionaryService;
	}

	@PostConstruct
	public void postConstruct() {
		assert (crudRepository != null);
		assert (dictionaryService != null);
	}

	@Override
	public ENTITY findOne(@NotNull ID id) {
		return crudRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+id+" not found"));
	}

	@Override
	public List<ENTITY> findByIds(List<ID> ids) {
		if (CollectionUtils.isEmpty(ids)){
			return Collections.emptyList();
		}
		List<ENTITY> entities = crudRepository.findAll();
		entities.removeAll(Collections.singleton(null));
		return entities;
	}

	@Override
	public ENTITY create(@Validated @NotNull ENTITY entity) {
		if (entity.getId() != null && crudRepository.existsById(entity.getId())) {
			throw new EntityAlreadyExistsException("Entity with id : " + entity.getId().toString() + ", already exists");
		}
		return crudRepository.save(entity);
	}

	@Override
	public ENTITY update(@NotNull ID id, @Validated @NotNull ENTITY entityToUpdate) {
		if (!crudRepository.existsById(id)){
			throw new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+id+" not found");
		}
		entityToUpdate.setId(id);
		return crudRepository.save(entityToUpdate);
	}

	@Override
	public void delete(ID id) {
		if (!crudRepository.existsById(id)){
			throw new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+ id +" not found");
		}
		crudRepository.deleteById(id);
	}

	@Override
	public void deleteById(@NotNull ID id) {
		if (!crudRepository.existsById(id)){
			throw new ResourceNotFoundException("Entity of type "+entityClassType.toString()+" with id : "+ id +" not found");
		}
		crudRepository.deleteById(id);
	}

	@Override
	public Page<ENTITY> search(@NotNull String searchQuery, PageRequest pageRequest) {
		Node rootNode = new RSQLParser(Operators.getOperators()).parse(searchQuery);
		Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>(getDictionaryService(), false));
		return crudRepository.findAll(spec, pageRequest);
	}
	

	@Override
	public List<ENTITY> search(@NotNull String searchQuery) {
		Node rootNode = new RSQLParser(Operators.getOperators()).parse(searchQuery);
		Specification<ENTITY> spec = rootNode.accept(new CustomRsqlVisitor<>(getDictionaryService(), true));
		List<ENTITY> content = crudRepository.findAll(spec);
		content.removeAll(Collections.singleton(null));
		return content;
	}
}
