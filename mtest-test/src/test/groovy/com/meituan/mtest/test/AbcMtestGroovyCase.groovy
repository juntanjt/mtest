package com.meituan.mtest.test;

import com.meituan.mtest.SpringBeanRegistryUtil;
import com.meituan.mtest.demo.user.dao.UserDAO;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import spock.lang.Specification;

abstract class AbcMtestGroovyCase extends Specification implements BeanFactoryPostProcessor {

    private static Object[] mocks;

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        SpringBeanRegistryUtil.registerSingleton(beanFactory, "userDAO", userDAO);

        AbcMtestGroovyCase.mocks = [userDAO];
    }

    void cleanup() {
        Mockito.clearInvocations(AbcMtestGroovyCase.mocks);
    }
}
