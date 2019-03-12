package com.debaets.crud.core.annotation.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class CrudServiceAnnotationProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Autowired
    public CrudServiceAnnotationProcessor(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.scanCrudServiceAnnotation(bean, beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    private void scanCrudServiceAnnotation(Object bean, String beanName) {
        this.configureFieldInjection(bean);
    }

    private void configureFieldInjection(Object bean) {
        Class<?> managedBeanClass = bean.getClass();
        var fieldCallback = new CrudServiceFieldCallback(configurableListableBeanFactory,bean);
        ReflectionUtils.doWithFields(managedBeanClass,fieldCallback);
    }
}
