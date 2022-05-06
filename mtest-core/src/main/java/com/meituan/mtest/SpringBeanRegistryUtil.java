package com.meituan.mtest;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 *
 * @author Jun Tan
 */
public class SpringBeanRegistryUtil {

    /**
     *
     * @param beanFactory
     * @param beanName
     * @param singletonObject
     */
    public static void registerSingleton(ConfigurableListableBeanFactory beanFactory, String beanName, Object singletonObject) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            ((BeanDefinitionRegistry) beanFactory).removeBeanDefinition(beanName);
        }
        beanFactory.registerSingleton(beanName, singletonObject);
    }

    /**
     *
     * @param beanFactory
     * @param dependencyType
     * @param singletonObject
     */
    public static void registerSingleton(ConfigurableListableBeanFactory beanFactory, Class<?> dependencyType, Object singletonObject) {
        beanFactory.registerResolvableDependency(dependencyType, singletonObject);
    }

}
