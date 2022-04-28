package com.meituan.mtest

import com.google.common.collect.Maps
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import spock.lang.Specification

abstract class MtestBaseCase extends Specification implements BeanFactoryPostProcessor {

    private static Map<String, Object> mockObjects = Maps.newHashMap()

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        TestMethod[] testMethods = getMockMethods();
        if (testMethods==null || testMethods.length==0) {
            return
        }
        for (TestMethod testMethod : testMethods) {
            if (mockObjects.containsKey(testMethod.getBeanName())) {
                continue
            }
            Object mockObject = Mockito.mock(testMethod.getTestClass())
            BeanFactoryPostProcessorUtil.registerSingleton(beanFactory, testMethod.getBeanName(), mockObject)

            mockObjects.put(testMethod.getBeanName(), mockObject)
        }
    }

    void setup() {

    }

    void cleanup() {
        if (! MtestBaseCase.mockObjects.isEmpty()) {
            Mockito.clearInvocations(MtestBaseCase.mockObjects.values().toArray())
        }
    }

    abstract TestMethod getTestMethod()

    abstract TestMethod[] getMockMethods()

    protected Iterable<TestCase> testCase() {
        TestMethod testMethod = getTestMethod()
        if (testMethod.overload == -1) {
            return DataLoaders.loadTestCases(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName())
        } else {
            return DataLoaders.loadTestCases(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), testMethod.getOverload())
        }
    }

    protected Iterable<Object[]> request() {
        TestMethod testMethod = getTestMethod()
        if (testMethod.overload == -1) {
            return DataLoaders.loadRequests(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName())
        } else {
            return DataLoaders.loadRequests(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), testMethod.getOverload())
        }
    }

    protected Iterable<Object> expected() {
        TestMethod testMethod = getTestMethod()
        if (testMethod.overload == -1) {
            return DataLoaders.loadResponses(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName())
        } else {
            return DataLoaders.loadResponses(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), testMethod.getOverload())
        }
    }

    protected void mock(TestCase testCase) {
        TestMethod testMethod = getTestMethod()
        TestMethod[] mockMethods = getMockMethods()
        if (testMethod.overload == -1) {
            MockMakers.mock(testCase, testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), mockMethods, mockObjects)
        } else {
            MockMakers.mock(testCase, testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), testMethod.getOverload(), mockMethods, mockObjects)
        }
    }

}
