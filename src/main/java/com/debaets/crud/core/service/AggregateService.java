package com.debaets.crud.core.service;

public interface AggregateService <DTO, ENTITY> {

	DTO aggregate(DTO dto, ENTITY entity);

}
