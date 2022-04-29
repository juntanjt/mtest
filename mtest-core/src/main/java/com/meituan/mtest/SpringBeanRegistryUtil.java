package com.meituan.mtest;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class SpringBeanRegistryUtil {

    public static void registerSingleton(ConfigurableListableBeanFactory beanFactory, TestMethod testMethod, Object singletonObject) {
        if (testMethod.getBeanName() != null) {
            registerSingleton(beanFactory, testMethod.getBeanName(), singletonObject);
        } else {
            registerSingleton(beanFactory, testMethod.getTestClass(), singletonObject);
        }
    }

    public static void registerSingleton(ConfigurableListableBeanFactory beanFactory, String beanName, Object singletonObject) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            ((BeanDefinitionRegistry) beanFactory).removeBeanDefinition(beanName);
        }
        beanFactory.registerSingleton(beanName, singletonObject);
    }

    public static void registerSingleton(ConfigurableListableBeanFactory beanFactory, Class<?> dependencyType, Object singletonObject) {
        beanFactory.registerResolvableDependency(dependencyType, singletonObject);
    }

}
