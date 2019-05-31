package com.debaets.crud.core.service;

import com.debaets.crud.core.model.GenericRsqlSpecification;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.Join;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor()
public class GenericRsqlSpecBuilder<T> {

	@NotNull
	private final DictionaryService dictionaryService;

	private final boolean isDistinct;
	private final Map<String, Join> joinMap;


	public Specification<T> createSpecification(Node node) {
		if (node instanceof LogicalNode) {
			return createSpecification((LogicalNode) node);
		}
		if (node instanceof ComparisonNode) {
			return createSpecification((ComparisonNode) node);
		}
		return null;
	}

	public Specification<T> createSpecification(LogicalNode logicalNode) {
		List<Specification<T>> specs = new ArrayList<>();
		Specification<T> temp;
		for (Node node : logicalNode.getChildren()) {
			temp = createSpecification(node);
			if (temp != null) {
				specs.add(temp);
			}
		}

		Specification<T> result = specs.get(0);
		if (logicalNode.getOperator() == LogicalOperator.AND) {
			for (int i = 1; i < specs.size(); i++) {
				result = Specification.where(result).and(specs.get(i));
			}
		} else if (logicalNode.getOperator() == LogicalOperator.OR) {
			for (int i = 1; i < specs.size(); i++) {
				result = Specification.where(result).or(specs.get(i));
			}
		}

		return result;
	}

	public Specification<T> createSpecification(ComparisonNode comparisonNode) {
		return Specification.where(
				new GenericRsqlSpecification<>(
						dictionaryService.convertToEntityName(comparisonNode.getSelector()),
						comparisonNode.getOperator(),
						comparisonNode.getArguments(),
						isDistinct,
						joinMap
				)
		);
	}
}
