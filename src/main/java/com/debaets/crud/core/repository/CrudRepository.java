package com.debaets.crud.core.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudRepository<ENTITY, ID extends Serializable> extends JpaRepository<ENTITY, ID>, JpaSpecificationExecutor<ENTITY> {
}
