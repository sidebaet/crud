package com.debaets.crud.core.model;

import java.util.Set;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class Operators {

	public static ComparisonOperator BETWEEN = new ComparisonOperator("=between=", true);

	public static Set<ComparisonOperator> getOperators(){
		Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
		operators.add(BETWEEN);
		return operators;
	}

}
