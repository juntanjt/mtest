package com.meituan.mtest.main;

import com.meituan.mtest.BeanFactoryPostProcessorUtil;
import com.meituan.mtest.main.user.dao.UserDAO;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import spock.lang.Specification;

public abstract class AbcMtestCase extends Specification implements BeanFactoryPostProcessor {

    private Object[] mocks;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        BeanFactoryPostProcessorUtil.registerSingleton(beanFactory, "userDAO", userDAO);

        mocks = new Object[] { userDAO };
    }

    public void cleanup() {
        Mockito.clearInvocations(mocks);
    }
}
