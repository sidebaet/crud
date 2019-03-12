package com.debaets.crud.core.annotation.service;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface CrudService {

    Class<?> entity();

}
