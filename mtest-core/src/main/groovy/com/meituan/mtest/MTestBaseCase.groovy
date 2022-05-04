package com.meituan.mtest

import com.google.common.base.Throwables
import com.google.common.collect.Maps
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

    private static final Map<Class, MockMaker> mockMakerMap = Maps.newHashMap()
    private static final Map<Class, DBTester> dbTesterMap = Maps.newHashMap()

    @Shared
    private final MockMaker mockMaker = MockMaker.newInstance()
    @Shared
    private final DBTester dbTester = DBTester.newInstance()
    @Shared
    private TestMethod sharedTestMethod

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
    protected MockMethod[] getMockMethods() {
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
    protected DataSource getDataSource() {
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

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Spring bean class name
        String className = this.getClass().getName().split('\\$')[0]
        Class thisClass = Class.forName(className)
        mockMakerMap.get(thisClass).initMockObjects(getMockMethods(), beanFactory)
        dbTesterMap.get(thisClass).initDatabaseTester(getDataSource())
    }

    /**
     * Spock setupSpec
     */
    void setupSpec() {
        mockMakerMap.put(this.getClass(), mockMaker)
        dbTesterMap.put(this.getClass(), dbTester)
        sharedTestMethod = getTestMethod()

        mockMaker.registerMockRequestChecker(getMockRequestChecker())
        dbTester.registerDBChecker(getDBChecker())
    }

    /**
     * Spock cleanupSpec
     */
    void cleanupSpec() {
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
        try {
            dbTester.verifyData(sharedTestMethod, MTestContext.getTestCase())
        } catch(Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
        } finally {
            try {
                mockMaker.cleanup()
            } catch(Exception e1) {
            }
            try {
                dbTester.cleanup()
            } catch(Exception e1) {
            }
        }
    }

}
