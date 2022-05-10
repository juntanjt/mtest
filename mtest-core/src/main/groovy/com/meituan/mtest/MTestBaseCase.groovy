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
    private MTestContext context = MTestContext.newInstance()

    @Shared
    private TestMethod sharedTestMethod

    /**
     *
     * @return
     */
    protected Iterable<TestCase> testCase() {
        return MTestContext.ContextIterable.of(DataLoaders.loadTestCases(getTestMethod()), context, MTestContext.KeyType.TEST_CASE)
    }

    /**
     *
     * @param testCaseType
     * @return
     */
    protected Iterable<TestCase> testCase(int testCaseType) {
        if (testCaseType == TestCase.NORMAL) {
            return MTestContext.ContextIterable.of(DataLoaders.loadTestCases(getTestMethod()), context, MTestContext.KeyType.TEST_CASE)
        } else if (testCaseType == TestCase.EXCEPTION) {
            return MTestContext.ContextIterable.of(DataLoaders.loadExceptionTestCases(getTestMethod()), context, MTestContext.KeyType.TEST_CASE)
        } else {
            throw new MTestException("testCaseType " + testCaseType + " is not exists")
        }
    }

    /**
     *
     * @return
     */
    protected Iterable<Object[]> request() {
        Iterable<TestCase> testCases = testCase()
        return MTestContext.ContextIterable.of(DataLoaders.loadRequests(getTestMethod(), testCases), context, MTestContext.KeyType.REQUEST)
    }

    /**
     *
     * @param testCaseType
     * @return
     */
    protected Iterable<Object[]> request(int testCaseType) {
        Iterable<TestCase> testCases = testCase(testCaseType)
        if (testCaseType == TestCase.NORMAL) {
            return MTestContext.ContextIterable.of(DataLoaders.loadRequests(getTestMethod(), testCases), context, MTestContext.KeyType.REQUEST)
        } else if (testCaseType == TestCase.EXCEPTION) {
            return MTestContext.ContextIterable.of(DataLoaders.loadRequests(getTestMethod(), testCases), context, MTestContext.KeyType.REQUEST)
        } else {
            throw new MTestException("testCaseType " + testCaseType + " is not exists")
        }
    }

    /**
     *
     * @return
     */
    protected Iterable<Object> expected() {
        Iterable<TestCase> testCases = testCase()
        return MTestContext.ContextIterable.of(DataLoaders.loadExpecteds(getTestMethod(), testCases), context, MTestContext.KeyType.EXPECTED)
    }

    /**
     *
     * @return
     */
    protected Iterable<Throwable> expectedException() {
        Iterable<TestCase> testCases = testCase(TestCase.EXCEPTION)
        return MTestContext.ContextIterable.of(DataLoaders.loadExceptions(getTestMethod(), testCases), context, MTestContext.KeyType.EXCEPTION)
    }

    /**
     * 
     * @return
     */
    protected MTestContext context() {
        return context
    }

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
    protected DBChecker getDBChecker() {
        return null
    }

    /**
     *
     * @return
     */
    protected DataSource getDataSource() {
        return null
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
        mockMaker.mock(sharedTestMethod, context.getTestCase())
        dbTester.setUp(sharedTestMethod, context.getTestCase())
    }

    /**
     * Spock cleanup
     */
    void cleanup() {
        try {
            dbTester.verifyData(sharedTestMethod, context.getTestCase())
        } catch(Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class)
            throw new MTestException("test case cleanup dbTester.verifyData error", e)
        } finally {
            Exception ee = null;
            try {
                mockMaker.cleanup()
            } catch(Exception e1) {
                ee = e1
            }
            try {
                dbTester.cleanup()
            } catch(Exception e1) {
                ee = e1
            }
            try {
                context.cleanup()
            } catch(Exception e1) {
                ee = e1
            }
            if (ee != null) {
                Throwables.propagateIfInstanceOf(ee, MTestException.class)
                throw new MTestException("test case cleanup error", ee)
            }
        }
    }

}
