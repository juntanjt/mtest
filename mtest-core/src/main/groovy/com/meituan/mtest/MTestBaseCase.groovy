package com.meituan.mtest

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

/**
 *
 * @author Jun Tan
 */
abstract class MTestBaseCase extends Specification implements BeanFactoryPostProcessor {

    private static final MockMaker mockMaker = MockMaker.newInstance()
    private static final DBTester dbTester = DBTester.newInstance()
    @Shared
    private TestMethod sharedTestMethod

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        mockMaker.initMockObjects(getMockMethods(), beanFactory)
        dbTester.initDatabaseTester(getDataSource())
    }

    /**
     * Spock setupSpec
     */
    void setupSpec() {
        sharedTestMethod = getTestMethod()
        mockMaker.registerMockRequestChecker(getMockRequestChecker())
        dbTester.registerDBChecker(getDBChecker())
        dbTester.registerDBCheckers(getDBCheckers())
    }

    /**
     * Spock setup
     */
    void setup() {
        mockMaker.mock(sharedTestMethod, MTestContext.getTestCase())
        dbTester.setUp(sharedTestMethod, MTestContext.getTestCase())
    }

    /**
     * Spock cleanup
     */
    void cleanup() {
        dbTester.verifyData(sharedTestMethod, MTestContext.getTestCase())

        mockMaker.cleanup()
        dbTester.cleanup()
    }

    /**
     *
     * @return
     */
    protected abstract MockMethod[] getMockMethods()

    /**
     *
     * @return
     */
    protected TestMethod getTestMethod() {
        MTest mTest = this.getClass().getAnnotation(MTest.class)
        if (mTest == null) {
            throw new MTestException("MTest Annotation is not exists")
        }
        return new TestMethod(mTest)
    }

    /**
     *
     * @return
     */
    protected DataSource getDataSource() {
        return null
    }

    /**
     *
     * @return
     */
    protected MockRequestChecker getMockRequestChecker() {
        return null
    }

    /**
     *
     * @return
     */
    protected DBChecker getDBChecker() {
        return null
    }

    /**
     *
     * @return
     */
    protected Map<String, DBChecker> getDBCheckers() {
        return null
    }

    /**
     *
     * @return
     */
    protected Iterable<TestCase> testCase() {
        return DataLoaders.loadTestCases(getTestMethod())
    }

    /**
     *
     * @return
     */
    protected Iterable<Object[]> request() {
        return DataLoaders.loadRequests(getTestMethod())
    }

    /**
     *
     * @return
     */
    protected Iterable<Object> expected() {
        return DataLoaders.loadExpecteds(getTestMethod())
    }

}
