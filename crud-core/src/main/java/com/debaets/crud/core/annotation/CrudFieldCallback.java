package com.debaets.crud.core.annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.debaets.crud.core.repository.CrudRepository;
import com.debaets.crud.core.service.DictionaryService;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class CrudFieldCallback {

	protected static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
	protected static String ERROR_CREATE_INSTANCE = "Cannot create instance of type '{}' or instance creation is failed because: {}";
	private static String WARN_NON_GENERIC_VALUE = "@Crud annotation assigned to raw (non-generic) declaration. This will make your code less type-safe";


	protected final ConfigurableListableBeanFactory configurableBeanFactory;

	protected CrudFieldCallback(ConfigurableListableBeanFactory configurableBeanFactory) {
		this.configurableBeanFactory = configurableBeanFactory;
	}

	protected Object getServiceBeanInstance(String beanName, Class<?> genericClass, Class<?> entityClass) {
		Object crudServiceInstance;
		if (!configurableBeanFactory.containsBean(beanName)) {
			log.info("Creating new CrudService bean named '{}'", beanName);

			Object toRegister;
			try {
				var constructor = genericClass.getConstructor(Class.class, CrudRepository.class, DictionaryService.class);
				var repositoryBeanName = entityClass.getSimpleName().toLowerCase()+"Repository";
				toRegister = constructor.newInstance(
						entityClass, (CrudRepository)configurableBeanFactory.getBean(repositoryBeanName), getDictionaryService(entityClass));
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

	private DictionaryService getDictionaryService(Class<?> entityClass) {
		var dictionaryBeanName = entityClass.getSimpleName().toLowerCase()+"DictionaryService";
		if (configurableBeanFactory.containsBean(dictionaryBeanName)){
			return (DictionaryService) configurableBeanFactory.getBean(dictionaryBeanName);
		}
		return new DictionaryService() {};
	}

	protected boolean genericTypeIsValid(Class<?> clazz, Type field, int typeIndex) {
		if (field instanceof ParameterizedType) {
			var parameterizedType = (ParameterizedType) field;
			var type = parameterizedType.getActualTypeArguments()[typeIndex];
			return type.equals(clazz);
		} else {
			log.warn(WARN_NON_GENERIC_VALUE);
			return true;
		}
	}
}
