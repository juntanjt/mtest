package com.meituan.mtest

import com.google.common.collect.Maps
import org.dbunit.IDatabaseTester
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import spock.lang.Specification

import javax.sql.DataSource

abstract class MtestBaseCase extends Specification implements BeanFactoryPostProcessor {

    private static final Map<String, Object> mockObjects = Maps.newHashMap()
    private static IDatabaseTester databaseTester

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
            SpringBeanRegistryUtil.registerSingleton(beanFactory, testMethod, mockObject)

            mockObjects.put(testMethod.getBeanName(), mockObject)
        }
        databaseTester = DbTesters.newDatabaseTester(getDataSource())
    }

    void setup() {
        TestMethod testMethod = getTestMethod()
        if (testMethod.overload == -1) {
            DbTesters.setUp(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), MtestContext.getTestCase(), databaseTester)
        } else {
            DbTesters.setUp(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), MtestContext.getTestCase(), testMethod.getOverload(), databaseTester)
        }
    }

    void cleanup() {
        if (! mockObjects.isEmpty()) {
            Mockito.clearInvocations(mockObjects.values().toArray())
        }
        TestMethod testMethod = getTestMethod()
        if (testMethod.overload == -1) {
            DbTesters.verifyData(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), MtestContext.getTestCase(), databaseTester)
        } else {
            DbTesters.verifyData(testMethod.getTestClass().getSimpleName(), testMethod.getMethod().getName(), MtestContext.getTestCase(), testMethod.getOverload(), databaseTester)
        }
        DbTesters.tearDown(databaseTester)
    }

    protected abstract TestMethod getTestMethod()

    protected abstract TestMethod[] getMockMethods()

    protected DataSource getDataSource() {
        return null
    }

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
