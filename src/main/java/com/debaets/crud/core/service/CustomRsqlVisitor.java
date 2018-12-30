package com.debaets.crud.core.service;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.Specification;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

public class CustomRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

	private GenericRsqlSpecBuilder<T> builder;

	public CustomRsqlVisitor(@NotNull DictionaryService dictionaryService, boolean isDistinct) {
		builder = new GenericRsqlSpecBuilder<>(dictionaryService, isDistinct);
	}

	@Override
	public Specification<T> visit(AndNode node, Void param) {
		return builder.createSpecification(node);
	}

	@Override
	public Specification<T> visit(OrNode node, Void param) {
		return builder.createSpecification(node);
	}

	@Override
	public Specification<T> visit(ComparisonNode node, Void params) {
		return builder.createSpecification(node);
	}
}
