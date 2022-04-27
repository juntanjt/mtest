package com.meituan.mtest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class BeanFactoryPostProcessorUtil {

    private SingletonBeanRegistry beanFactory;
    private BeanDefinitionRegistry beanDefinitionRegistry;

    public static void registerSingleton(BeanFactory beanFactory, String beanName, Object singletonObject) {
        SingletonBeanRegistry singletonBeanRegistry = (SingletonBeanRegistry) beanFactory;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
        if (beanDefinitionRegistry.containsBeanDefinition(beanName)) {
            beanDefinitionRegistry.removeBeanDefinition(beanName);
        }
        singletonBeanRegistry.registerSingleton(beanName, singletonObject);
    }

}
