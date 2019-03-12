package com.debaets.crud.core.annotation;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Log4j2
public class CrudServiceFieldCallback implements ReflectionUtils.FieldCallback {

    private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    private static String ERROR_ENTITY_VALUE_NOT_SAME = "@CrudService(entity) value should have same type with injected generic type";
    private static String WARN_NON_GENERIC_VALUE = "@CrudService annotation assigned to raw (non-generic) declaration. This will make your code less type-safe";
    private static String ERROR_CREATE_INSTANCE = "Cannot create instance of type '{}' or instance creation is failed because: {}";

    private ConfigurableListableBeanFactory configurableBeanFactory;
    private Object bean;

    public CrudServiceFieldCallback(ConfigurableListableBeanFactory configurableListableBeanFactory, Object bean) {
        this.configurableBeanFactory = configurableListableBeanFactory;
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

        if (genericTypeIsValid(classValue, fieldGenericType)) {
            var beanName = classValue.getSimpleName() + generic.getSimpleName();
            var beanInstance = getBeanInstance(beanName, generic, classValue);
            field.set(bean, beanInstance);
        } else {
            throw new IllegalArgumentException(ERROR_ENTITY_VALUE_NOT_SAME);
        }
    }

    private boolean genericTypeIsValid(Class<?> clazz, Type field) {
        if (field instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) field;
            var type = parameterizedType.getActualTypeArguments()[0];
            return type.equals(clazz);
        } else {
            log.warn(WARN_NON_GENERIC_VALUE);
            return true;
        }
    }

    private Object getBeanInstance(String beanName, Class<?> genericClass, Class<?> paramClass) {
        Object crudServiceInstance;
        if (!configurableBeanFactory.containsBean(beanName)) {
            log.info("Creating new CrudService bean named '{}'", beanName);

            Object toRegister;
            try {
                var constructor = genericClass.getConstructor(Class.class);
                toRegister = constructor.newInstance(paramClass);
            } catch (Exception e){
                log.error(ERROR_CREATE_INSTANCE, genericClass.getTypeName(), e);
                throw new RuntimeException(e);
            }

            crudServiceInstance = configurableBeanFactory.initializeBean(toRegister, beanName);
            configurableBeanFactory.autowireBeanProperties(crudServiceInstance, AUTOWIRE_MODE, true);
            configurableBeanFactory.registerSingleton(beanName, crudServiceInstance);
            log.info("Bean named '{}' created successfully.", beanName);
        } else {
            crudServiceInstance = configurableBeanFactory.getBean(beanName);
            log.info("Bean named '{}' already exists used as current bean reference.", beanName);
        }
        return crudServiceInstance;
    }
}
