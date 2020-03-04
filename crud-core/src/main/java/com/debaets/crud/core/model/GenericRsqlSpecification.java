package com.debaets.crud.core.model;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.lang.StringUtils;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.debaets.crud.core.model.Operators.IS_NULL;

@SuppressWarnings("unchecked")
public class GenericRsqlSpecification<T> implements Specification<T> {
	private String property;
	private ComparisonOperator operator;
	private List<String> arguments;
	private boolean isDistinct;
	private Map<String, Join> joinMap;
	private Set<CriteriaQuery<?>> knownQueries;
	private boolean canJoin;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public GenericRsqlSpecification(String property, ComparisonOperator operator, List<String> arguments,
									boolean isDistinct, Map<String, Join> joinMap) {
		super();
		this.property = property;
		this.operator = operator;
		this.arguments = arguments;

		this.isDistinct = isDistinct;
		this.joinMap = joinMap;
		this.knownQueries = new HashSet<>();
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		canJoin = !knownQueries.contains(query);
		knownQueries.add(query);
		List<Object> args = null;
		Object argument = null;
		if (this.operator != IS_NULL){
			args = castArguments(root);
			argument = args.get(0);
		}
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
			case IS_NULL:
				return getIsNull(builder, args, path);
		}

		return null;
	}

	private Predicate getIsNull(CriteriaBuilder builder, Object argument, Path<String> path) {
		return builder.isNull(path);
	}

	private Predicate getBetween(CriteriaBuilder builder, List<Object> args, Path<String> path) {

		Object arg = args.get(0);
		if (arg instanceof Date) {
			return builder.between(path.as(Date.class), (Date) arg, (Date) args.get(1));
		} else if (arg instanceof OffsetDateTime) {
			return builder.between(path.as(OffsetDateTime.class), (OffsetDateTime) arg, (OffsetDateTime) args.get(1));
		}
		return builder.between(path.as(LocalDate.class), (LocalDate) arg, (LocalDate) args.get(1));
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
		} else if (argument instanceof LocalDate) {
			return builder.lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) argument);
		} else if (argument instanceof OffsetDateTime) {
			return builder.lessThanOrEqualTo(path.as(OffsetDateTime.class), (OffsetDateTime) argument);
		}
		return builder.lessThanOrEqualTo(
				path, argument.toString());
	}

	private Predicate getLessThan(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.lessThan(path.as(Date.class), (Date) argument);
		} else if (argument instanceof LocalDate) {
			return builder.lessThan(path.as(LocalDate.class), (LocalDate) argument);
		} else if (argument instanceof OffsetDateTime) {
			return builder.lessThan(path.as(OffsetDateTime.class), (OffsetDateTime) argument);
		}
		return builder.lessThan(path, argument.toString());
	}

	private Predicate getGreaterThanOrEquals(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.greaterThanOrEqualTo(path.as(Date.class), (Date) argument);
		} else if (argument instanceof LocalDate) {
			return builder.greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) argument);
		} else if (argument instanceof OffsetDateTime) {
			return builder.greaterThanOrEqualTo(path.as(OffsetDateTime.class), (OffsetDateTime) argument);
		}
		return builder.greaterThanOrEqualTo(
				path, argument.toString());
	}

	private Predicate getGreaterThan(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof Date) {
			return builder.greaterThan(path.as(Date.class), (Date) argument);
		} else if (argument instanceof LocalDate) {
			return builder.greaterThan(path.as(LocalDate.class), (LocalDate) argument);
		}  else if (argument instanceof OffsetDateTime) {
			return builder.greaterThan(path.as(OffsetDateTime.class), (OffsetDateTime) argument);
		}
		return builder.greaterThan(path, argument.toString());
	}

	private Predicate getNotEquals(CriteriaBuilder builder, Object argument, Path<String> path) {
		if (argument instanceof String) {
			return builder.notLike(
					builder.upper(path), argument.toString().toUpperCase().replace('*', '%'));
		} else if (argument instanceof Date) {
			return builder.notEqual(path.as(Date.class), argument);
		} else if (argument instanceof LocalDate) {
			return builder.notEqual(path.as(LocalDate.class), argument);
		} else if (argument instanceof OffsetDateTime) {
			return builder.notEqual(path.as(OffsetDateTime.class), argument);
		} else if (argument == null) {
			return builder.isNotNull(path);
		} else {
			return builder.notEqual(path, argument);
		}
	}

	private Predicate getEquals(CriteriaBuilder builder, Object argument, Path<?> path) {
		var type = path.getJavaType();
		if (argument == null) {
			return builder.isNull(path);
		}
		if (type.equals(String.class)) {
			String argumentString = argument.toString();
			if (argumentString.contains("*")) {
				return builder.like(
						builder.upper(path.as(String.class)), argumentString.toUpperCase().replace('*', '%'));
			}
			return builder.equal(builder.upper(path.as(String.class)), argumentString.toUpperCase());
		} else if (type.equals(Date.class)) {
			return builder.equal(path.as(Date.class), argument);
		} else if (type.equals(LocalDate.class)) {
			return builder.equal(path.as(LocalDate.class), argument);
		} else if (type.equals(OffsetDateTime.class)) {
			return builder.equal(path.as(OffsetDateTime.class), argument);
		} else if (type.equals(Boolean.class)) {
			return builder.equal(path.as(Boolean.class), Boolean.valueOf(argument.toString()));
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
			} else if (type.equals(LocalDate.class)) {
				args.add(LocalDate.parse(argument, formatter));
			} else if (type.equals(OffsetDateTime.class)) {
				var date = LocalDate.parse(argument, formatter);
				args.add(OffsetDateTime.of(
						date,
						LocalTime.MIN,
						OffsetDateTime.now().getOffset()
				));
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
			var propertyJoin = properties[0];
			if (canJoin) {
				var join = root.join(propertyJoin, JoinType.LEFT);
				if (!this.joinMap.containsKey(propertyJoin)) {
					this.joinMap.put(propertyJoin, join);
					//root.fetch(propertyJoin);
				}
				canJoin = false;
			}
			path = this.joinMap.get(propertyJoin);
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
