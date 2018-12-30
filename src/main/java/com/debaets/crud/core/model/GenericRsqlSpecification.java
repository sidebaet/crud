package com.debaets.crud.core.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jpa.criteria.path.PluralAttributePath;
import org.springframework.data.jpa.domain.Specification;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

@SuppressWarnings("unchecked")
public class GenericRsqlSpecification<T> implements Specification<T> {
	private String property;
	private ComparisonOperator operator;
	private List<String> arguments;
	private boolean isDistinct;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	public GenericRsqlSpecification(String property, ComparisonOperator operator, List<String> arguments,
			boolean isDistinct) {
		super();
		this.property = property;
		this.operator = operator;
		this.arguments = arguments;

		this.isDistinct = isDistinct;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Object> args = castArguments(root);
		Object argument = args.get(0);
		Path<String> path = (Path<String>) getPath(root, property);
		query.distinct(isDistinct);
		return getPredicate(builder, args, argument, path);
	}

	private Predicate getPredicate(CriteriaBuilder builder, List<Object> args, Object argument, Path<String> path) {
		switch (Objects.requireNonNull(RsqlSearchOperation.getSimpleOperator(operator))) {

		case EQUAL:
			return getEquals(builder, argument, path);
		case NOT_EQUAL:
			return getNotEquals(builder, argument, path);
		case GREATER_THAN:
			return getGreaterThan(builder, argument, path);
		case GREATER_THAN_OR_EQUAL:
			return getGreaterThanOrEquals(builder, argument, path);
		case LESS_THAN:
			return getLessThan(builder, argument, path);
		case LESS_THAN_OR_EQUAL:
			return getLessThanOrEqual(builder, argument, path);
		case IN:
			return getIn(args, path);
		case NOT_IN:
			return getIn(builder, args, path);
		case BETWEEN:
			return getBetween(builder, args, path);
		}

		return null;
	}

	private Predicate getBetween(CriteriaBuilder builder, List<Object> args, Path<String> path) {
		return builder.between(path.as(Date.class), (Date) args.get(0), (Date) args.get(1));
	}

	private Predicate getIn(CriteriaBuilder builder, List<Object> args, Path<String> path) {
		return builder.not(path.in(args));
	}

	private Predicate getIn(List<Object> args, Path<String> path) {
		return path.in(args);
	}

	private Predicate getLessThanOrEqual(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.lessThanOrEqualTo(path.as(Date.class), (Date) argument);
		}
		return builder.lessThanOrEqualTo(
				path, argument.toString());
	}

	private Predicate getLessThan(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.lessThan(path.as(Date.class), (Date) argument);
		}
		return builder.lessThan(path, argument.toString());
	}

	private Predicate getGreaterThanOrEquals(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.greaterThanOrEqualTo(path.as(Date.class), (Date) argument);
		}
		return builder.greaterThanOrEqualTo(
				path, argument.toString());
	}

	private Predicate getGreaterThan(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.greaterThan(path.as(Date.class), (Date) argument);
		}
		return builder.greaterThan(path, argument.toString());
	}

	private Predicate getNotEquals(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof String) {
			return builder.notLike(
					builder.upper(path), argument.toString().toUpperCase().replace('*', '%'));
		} else if (argument instanceof Date) {
			return builder.notEqual(path.as(Date.class), argument);
		} else if (argument == null) {
			return builder.isNotNull(path);
		} else {
			return builder.notEqual(path, argument);
		}
	}

	private Predicate getEquals(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof String) {
			String argumentString = argument.toString();
			if (argumentString.contains("*")) {
				return builder.like(
						builder.upper(path), argumentString.toUpperCase().replace('*', '%'));
			}
			return builder.equal(builder.upper(path), argumentString.toUpperCase());
		} else if (argument instanceof Date) {
			return builder.equal(path.as(Date.class), argument);
		} else if (argument == null) {
			return builder.isNull(path);
		} else {
			return builder.equal(path, argument);
		}
	}

	private List<Object> castArguments(Root<T> root) {
		List<Object> args = new ArrayList<>();

		Class<? extends T> type = getPath(root, property).getJavaType();

		for (String argument : arguments) {
			if (type.equals(Integer.class)) {
				args.add(Integer.parseInt(argument));
			} else if (type.equals(Long.class)) {
				args.add(Long.parseLong(argument));
			} else if (type.equals(Date.class)) {
				try {
					args.add(simpleDateFormat.parse(argument));
				} catch (ParseException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			} else if (type.getEnumConstants() != null && type.getEnumConstants().length > 0) {
				args.add(Enum.valueOf((Class<? extends Enum>) type, argument));
			} else {
				args.add(argument);
			}
		}

		return args;
	}

	private Path<T> getPath(Root<T> root, String property) {
		if (!StringUtils.contains(property, '.')) {
			return root.get(property);
		}
		String[] properties = property.split("\\.");
		Path<T> path = root.get(properties[0]);

		if (path instanceof PluralAttributePath) {
			path = root.join(properties[0]);
		}

		for (int i = 1; i < properties.length; ++i) {
			path = path.get(properties[i]);
		}
		if (path == null) {
			throw new RuntimeException("Cannot find property : " + property);
		}
		return path;
	}
}
