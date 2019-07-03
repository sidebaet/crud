package com.debaets.crud.core.annotation.facade;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface CrudFacade {

	Class<?> dto();
	Class<?> entity();

}
