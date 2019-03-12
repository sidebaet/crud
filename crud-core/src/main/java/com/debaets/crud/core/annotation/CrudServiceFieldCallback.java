package com.debaets.crud.core.annotation;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Log4j
public class CrudServiceFieldCallback implements ReflectionUtils.FieldCallback {

    private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    public CrudServiceFieldCallback(ConfigurableListableBeanFactory configurableListableBeanFactory, Object bean) {

    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

    }
}
