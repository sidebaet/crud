package com.debaets.crud.core.annotation.facade;

import com.debaets.crud.core.annotation.CrudFieldCallback;
import com.debaets.crud.core.service.CrudService;
import com.debaets.crud.core.service.CrudServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Log4j2
public class CrudFacadeFieldCallback
	extends CrudFieldCallback
	implements ReflectionUtils.FieldCallback
{
	private static String ERROR_ENTITY_VALUE_NOT_SAME = "@CrudFacade(entity) value should have same type with injected generic type";
	private Object bean;

	public CrudFacadeFieldCallback(ConfigurableListableBeanFactory configurableListableBeanFactory, Object bean) {
		super(configurableListableBeanFactory);
		this.bean = bean;
	}

	@Override
	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
		if (!field.isAnnotationPresent(CrudFacade.class)){
			return;
		}

		ReflectionUtils.makeAccessible(field);
		Type fieldGenericType = field.getGenericType();
		Class<?> generic = field.getType();
		Class<?> entityValue =  field.getDeclaredAnnotation(CrudFacade.class).entity();
		Class<?> dtoValue = field.getDeclaredAnnotation(CrudFacade.class).dto();

		if (genericTypeIsValid(dtoValue, fieldGenericType, 0)
			&& genericTypeIsValid(entityValue, fieldGenericType, 1)) {
			var beanName = dtoValue.getSimpleName() + entityValue.getSimpleName() + generic.getSimpleName();
			var beanInstance = getBeanInstance(beanName, generic, entityValue, dtoValue);
			field.set(bean, beanInstance);
		} else {
			throw new IllegalArgumentException(ERROR_ENTITY_VALUE_NOT_SAME);
		}
	}

	protected Object getBeanInstance(String beanName, Class<?> genericClass, Class<?> entityClass, Class<?> dtoClass) {
		Object crudFacadeInstance;
		if (!configurableBeanFactory.containsBean(beanName)) {
			log.info("Creating new CrudFacade bean named '{}'", beanName);

			Object toRegister;
			try {
				var constructor = genericClass.getConstructor(Class.class, Class.class, ConversionService.class, CrudService.class);
				var serviceBeanName = getServiceName(entityClass);
				var serviceBeanInstance = (CrudServiceImpl)getServiceBeanInstance(serviceBeanName, CrudServiceImpl.class, entityClass);
				toRegister = constructor.newInstance(
						dtoClass,entityClass, configurableBeanFactory.getBean(ConversionService.class), serviceBeanInstance);
			} catch (Exception e){
				log.error(ERROR_CREATE_INSTANCE, genericClass.getTypeName(), e);
				throw new RuntimeException(e);
			}

			crudFacadeInstance = configurableBeanFactory.initializeBean(toRegister, beanName);
			configurableBeanFactory.autowireBeanProperties(crudFacadeInstance, AUTOWIRE_MODE, true);
			configurableBeanFactory.registerSingleton(beanName, crudFacadeInstance);
			log.info("Bean named '{}' created successfully.", beanName);
		} else {
			crudFacadeInstance = configurableBeanFactory.getBean(beanName);
			log.info("Bean named '{}' already exists used as current bean reference.", beanName);
		}
		return crudFacadeInstance;
	}

	public static String getServiceName(Class<?> entityClass){
		return entityClass.getSimpleName().toLowerCase()+"CrudServiceImpl";
	}

}
