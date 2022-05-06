package com.meituan.mtest;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jun Tan
 */
public class MockMaker {

    private List<MockMethod> mockMethods;
    private final Map<String, Object> mockBeanObjects = Maps.newHashMap();
    private final Map<Class<?>, Object> mockClassObjects = Maps.newHashMap();
    private final Map<Class<?>, MockedStatic<?>> mockClassStatics = Maps.newHashMap();

    private MockRequestChecker mockRequestChecker = new DefaultMockRequestChecker();

    /**
     *
     */
    private MockMaker() {
    }

    /**
     *
     * @return
     */
    public static MockMaker newInstance() {
        return new MockMaker();
    }

    /**
     *
     * @param mockMethods
     * @param beanFactory
     */
    public void initMockObjects(MockMethod[] mockMethods, ConfigurableListableBeanFactory beanFactory) {
        try {
            if (mockMethods==null || mockMethods.length==0) {
                return;
            }
            this.mockMethods = Lists.newArrayList(mockMethods);

            List<MockMethod> nonStaticMockMethods = Lists.newArrayList();
            List<MockMethod> staticMockMethods = Lists.newArrayList();

            for (MockMethod mockMethod : mockMethods) {
                if (Modifier.isStatic(mockMethod.getMethod().getModifiers())) {
                    staticMockMethods.add(mockMethod);
                } else {
                    nonStaticMockMethods.add(mockMethod);
                }
            }
            initNonStaticMockObjects(nonStaticMockMethods, beanFactory);
            initStaticMockObjects(staticMockMethods);
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock init error", e);
        }
    }

    /**
     *
     * @param mockMethods
     * @param beanFactory
     */
    public void initNonStaticMockObjects(List<MockMethod> mockMethods, ConfigurableListableBeanFactory beanFactory) {
        if (mockMethods==null || mockMethods.isEmpty()) {
            return;
        }
        Map<String, MockMethod> beanNameMap = Maps.newHashMap();
        Map<Class<?>, MockMethod> testClassMap = Maps.newHashMap();
        for (MockMethod mockMethod : mockMethods) {
            if (mockMethod.getBeanName() != null) {
                beanNameMap.put(mockMethod.getBeanName(), mockMethod);
            } else {
                testClassMap.put(mockMethod.getTestClass(), mockMethod);
            }
        }
        for (String beanName : beanNameMap.keySet()) {
            Object mockObject = Mockito.mock(beanNameMap.get(beanName).getTestClass());
            SpringBeanRegistryUtil.registerSingleton(beanFactory, beanName, mockObject);
            mockBeanObjects.put(beanName, mockObject);
        }
        for (Class<?> testClass : testClassMap.keySet()) {
            Object mockObject = Mockito.mock(testClassMap.get(testClass).getTestClass());
            SpringBeanRegistryUtil.registerSingleton(beanFactory, testClass, mockObject);
            mockClassObjects.put(testClass, mockObject);
        }
    }

    /**
     *
     * @param mockMethods
     */
    public void initStaticMockObjects(List<MockMethod> mockMethods) {
        if (mockMethods==null || mockMethods.isEmpty()) {
            return;
        }
        Map<Class<?>, MockMethod> testClassMap = Maps.newHashMap();
        for (MockMethod mockMethod : mockMethods) {
            testClassMap.put(mockMethod.getTestClass(), mockMethod);
        }
        for (Class<?> testClass : testClassMap.keySet()) {
            MockedStatic<?> mockedStatic = Mockito.mockStatic(testClass);
            mockClassStatics.put(testClass, mockedStatic);
        }
    }

    /**
     *
     * @param mockRequestChecker
     */
    public void registerMockRequestChecker(MockRequestChecker mockRequestChecker) {
        if (mockRequestChecker != null) {
            this.mockRequestChecker = mockRequestChecker;
        }
    }

    /**
     *
     * @param testMethod
     * @param testCase
     */
    public void mock(TestMethod testMethod, TestCase testCase) {
        try {
            Map<Mocker, List<Object[]>> mockRequests = DataLoaders.loadMockRequests(testMethod, testCase);
            Map<Mocker, List<Object>> mockResponses = DataLoaders.loadMockResponses(testMethod, testCase);
            mock(mockRequests, mockResponses);
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock error", e);
        }
    }

    /**
     *
     * @param testMethodPath
     * @param testCase
     */
    public void mock(String testMethodPath, TestCase testCase) {
        try {
            Map<Mocker, List<Object[]>> mockRequestMap = DataLoaders.loadMockRequests(testMethodPath, testCase);
            Map<Mocker, List<Object>> mockResponseMap = DataLoaders.loadMockResponses(testMethodPath, testCase);
            mock(mockRequestMap, mockResponseMap);
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock error", e);
        }
    }

    /**
     *
     * @param mockRequestMap
     * @param mockResponseMap
     */
    private void mock(Map<Mocker, List<Object[]>> mockRequestMap, Map<Mocker, List<Object>> mockResponseMap) {
        if (mockMethods==null || mockMethods.isEmpty()) {
            return;
        }
        for (MockMethod mockMethod : mockMethods) {
            Mocker mocker = new Mocker(mockMethod.getTestClass().getSimpleName(), mockMethod.getMethod().getName(), mockMethod.getOverload());

            List<Object[]> mockRequests = mockRequestMap != null ? mockRequestMap.get(mocker) : null;
            List<Object> mockResponse = mockResponseMap != null ? mockResponseMap.get(mocker) : null;

            if (Modifier.isStatic(mockMethod.getMethod().getModifiers())) {
                mockStatic(mockMethod, mocker, mockRequests, mockResponse);
            } else {
                mock(mockMethod, mocker, mockRequests, mockResponse);
            }
        }
    }

    /**
     *
     * @param mockMethod
     * @param mocker
     * @param mockRequests
     * @param mockResponses
     */
    private void mock(MockMethod mockMethod, Mocker mocker, List<Object[]> mockRequests, List<Object> mockResponses) {
        try {
            Object mockObject = getMockObject(mockMethod);
            List<Object> args = Lists.newArrayList();
            for (Class<?> parameterType : mockMethod.getMethod().getParameterTypes()) {
                args.add(Mockito.any(parameterType));
            }
            OngoingStubbing<?> ongoingStubbing = Mockito.when(mockMethod.getMethod().invoke(mockObject, args.toArray()));

            for (int i=0; ; i++) {
                if ((mockRequests == null || mockRequests.size() <= i) && (mockResponses == null || mockResponses.size() <= i)) {
                    break;
                }
                final Object[] mockRequest = (mockRequests != null && mockRequests.size()>i) ? mockRequests.get(i) : null;
                final Object mockResponse = (mockResponses != null && mockResponses.size()>i) ? mockResponses.get(i) : null;

                Answer<?> answer = invocationOnMock -> {
                    mockRequestChecker.assertEquals(mocker, mockRequest, invocationOnMock.getArguments());
                    return mockResponse;
                };

                ongoingStubbing = ongoingStubbing.thenAnswer(answer);
            }
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock error", e);
        }
    }

    /**
     *
     * @param mockMethod
     * @param mocker
     * @param mockRequests
     * @param mockResponses
     */
    private void mockStatic(MockMethod mockMethod, Mocker mocker, List<Object[]> mockRequests, List<Object> mockResponses) {
        try {
            MockedStatic<?> mockedStatic = mockClassStatics.get(mockMethod.getTestClass());
            List<Object> args = Lists.newArrayList();
            for (Class<?> parameterType : mockMethod.getMethod().getParameterTypes()) {
                args.add(Mockito.any(parameterType));
            }
            OngoingStubbing<?> ongoingStubbing = mockedStatic.when(() -> mockMethod.getMethod().invoke(null, args.toArray()));

            for (int i=0; ; i++) {
                if ((mockRequests == null || mockRequests.size() <= i) && (mockResponses == null || mockResponses.size() <= i)) {
                    break;
                }
                final Object[] mockRequest = (mockRequests != null && mockRequests.size()>i) ? mockRequests.get(i) : null;
                final Object mockResponse = (mockResponses != null && mockResponses.size()>i) ? mockResponses.get(i) : null;

                Answer<?> answer = invocationOnMock -> {
                    mockRequestChecker.assertEquals(mocker, mockRequest, invocationOnMock.getArguments());
                    return mockResponse;
                };

                ongoingStubbing = ongoingStubbing.thenAnswer(answer);
            }
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock error", e);
        }
    }

    /**
     *
     */
    public void cleanup() {
        try {
            List<Object> mockObjects = Lists.newArrayList();
            mockObjects.addAll(mockBeanObjects.values());
            mockObjects.addAll(mockClassObjects.values());
            if (! mockObjects.isEmpty()) {
                Mockito.reset(mockObjects.toArray());
            }
        } catch (Exception e) {
        }
        try {
            for (MockedStatic<?> mockedStatic : mockClassStatics.values()) {
                mockedStatic.close();
            }
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param mockMethod
     * @return
     */
    private Object getMockObject(MockMethod mockMethod) {
        if (mockMethod.getBeanName() != null) {
            return mockBeanObjects.get(mockMethod.getBeanName());
        }
        return mockClassObjects.get(mockMethod.getTestClass());
    }

}
