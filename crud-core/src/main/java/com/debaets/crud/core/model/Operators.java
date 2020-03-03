package com.debaets.crud.core.model;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Set;

public class Operators {

	public static ComparisonOperator BETWEEN = new ComparisonOperator("=between=", true);
	public static ComparisonOperator IS_NULL = new ComparisonOperator("=isNull=", false);

	public static Set<ComparisonOperator> getOperators(){
		Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
		operators.add(BETWEEN);
		operators.add(IS_NULL);
		return operators;
	}

}
