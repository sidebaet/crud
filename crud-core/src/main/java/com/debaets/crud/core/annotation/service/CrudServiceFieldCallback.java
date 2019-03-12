package com.debaets.crud.core.annotation.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.debaets.crud.core.annotation.CrudFieldCallback;

@Log4j2
public class CrudServiceFieldCallback
        extends CrudFieldCallback
        implements ReflectionUtils.FieldCallback {

    private static String ERROR_ENTITY_VALUE_NOT_SAME = "@CrudService(entity) value should have same type with injected generic type";

    private Object bean;

    public CrudServiceFieldCallback(ConfigurableListableBeanFactory configurableListableBeanFactory, Object bean) {
        super(configurableListableBeanFactory);
        this.bean = bean;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAnnotationPresent(CrudService.class)){
            return;
        }

        ReflectionUtils.makeAccessible(field);
        Type fieldGenericType = field.getGenericType();
        Class<?> generic = field.getType();
        Class<?> classValue =  field.getDeclaredAnnotation(CrudService.class).entity();

        if (genericTypeIsValid(classValue, fieldGenericType, 0)) {
            var beanName = classValue.getSimpleName() + generic.getSimpleName();
            var beanInstance = getServiceBeanInstance(beanName, generic, classValue);
            field.set(bean, beanInstance);
        } else {
            throw new IllegalArgumentException(ERROR_ENTITY_VALUE_NOT_SAME);
        }
    }
}
