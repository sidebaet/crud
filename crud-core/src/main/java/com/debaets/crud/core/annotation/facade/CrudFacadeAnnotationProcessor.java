package com.debaets.crud.core.annotation.facade;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;


@Component
public class CrudFacadeAnnotationProcessor implements BeanPostProcessor {

	private final ConfigurableListableBeanFactory configurableListableBeanFactory;

	@Autowired
	public CrudFacadeAnnotationProcessor(ConfigurableListableBeanFactory configurableListableBeanFactory) {
		this.configurableListableBeanFactory = configurableListableBeanFactory;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		this.scanCrudFacadeAnnotation(bean, beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return null;
	}

	private void scanCrudFacadeAnnotation(Object bean, String beanName) {
		this.configureFieldInjection(bean);
	}

	private void configureFieldInjection(Object bean) {
		Class<?> managedBeanClass = bean.getClass();
		var fieldCallback = new CrudFacadeFieldCallback(configurableListableBeanFactory,bean);
		ReflectionUtils.doWithFields(managedBeanClass,fieldCallback);
	}
}
