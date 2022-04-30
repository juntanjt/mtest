package com.meituan.mtest;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jun Tan
 */
public class MockMaker {

    private MockMethod[] mockMethods;
    private final Map<String, Object> mockBeanObjects = Maps.newHashMap();
    private final Map<Class, Object> mockClassObjects = Maps.newHashMap();

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
            this.mockMethods = mockMethods;
            for (MockMethod mockMethod : mockMethods) {
                Object mockObject = getMockObject(mockMethod);
                if (mockObject != null) {
                    continue;
                }
                mockObject = Mockito.mock(mockMethod.getTestClass());
                if (mockMethod.getBeanName() != null) {
                    SpringBeanRegistryUtil.registerSingleton(beanFactory, mockMethod, mockObject);
                    mockBeanObjects.put(mockMethod.getBeanName(), mockObject);
                } else {
                    SpringBeanRegistryUtil.registerSingleton(beanFactory, mockMethod.getTestClass(), mockObject);
                    mockClassObjects.put(mockMethod.getTestClass(), mockObject);
                }
            }
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock init error", e);
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
            Map<Mocker, Object[]> mockRequests = DataLoaders.loadMockRequests(testMethod, testCase);
            Map<Mocker, Object> mockResponses = DataLoaders.loadMockResponses(testMethod, testCase);
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
            Map<Mocker, Object[]> mockRequests = DataLoaders.loadMockRequests(testMethodPath, testCase);
            Map<Mocker, Object> mockResponses = DataLoaders.loadMockResponses(testMethodPath, testCase);
            mock(mockRequests, mockResponses);
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock error", e);
        }
    }

    /**
     *
     * @param mockRequests
     * @param mockResponses
     */
    private void mock(Map<Mocker, Object[]> mockRequests, Map<Mocker, Object> mockResponses) {

        Map<Mocker, List<Mocker>> requestMockerMap = getRequestMockers(mockRequests);
        Map<Mocker, List<Mocker>> responseMockerMap = getResponseMockers(mockResponses);

        for (MockMethod mockMethod : mockMethods) {
            Mocker baseMocker = new Mocker();
            baseMocker.setClassSimpleName(mockMethod.getTestClass().getSimpleName());
            baseMocker.setMethodName(mockMethod.getMethod().getName());
            baseMocker.setOverload(mockMethod.getOverload());

            mock(mockMethod, requestMockerMap.get(baseMocker), responseMockerMap.get(baseMocker), mockRequests, mockResponses);
        }
    }

    /**
     *
     * @param mockMethod
     * @param requestMockers
     * @param responseMockers
     * @param mockRequests
     * @param mockResponses
     */
    private void mock(MockMethod mockMethod, List<Mocker> requestMockers, List<Mocker> responseMockers, Map<Mocker, Object[]> mockRequests, Map<Mocker, Object> mockResponses) {
        try {
            Set<Mocker> mockerSet = Sets.newHashSet();
            if (requestMockers != null) {
                mockerSet.addAll(requestMockers);
            }
            if (responseMockers != null) {
                mockerSet.addAll(responseMockers);
            }
            List<Mocker> mockers = Lists.newArrayList(mockerSet);
            mockers.sort((o1, o2) -> {
                if (o1.equals(o2)) return 0;
                if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
                if (o1.getMethodName().compareTo(o2.getMethodName())!=0) return o1.getMethodName().compareTo(o2.getMethodName());
                if (o1.getOverload() != o2.getOverload()) return o1.getOverload() - o2.getOverload();
                return o1.getOrder() - o2.getOrder();
            });

            Object mockObject = getMockObject(mockMethod);
            List<Object> args = Lists.newArrayList();
            for (Class parameterType : mockMethod.getMethod().getParameterTypes()) {
                args.add(Mockito.any(parameterType));
            }
            OngoingStubbing ongoingStubbing = Mockito.when(mockMethod.getMethod().invoke(mockObject, args.toArray()));

            for (Mocker mocker : mockers) {
                final Object[] mockRequest = (mockRequests != null) ? mockRequests.get(mocker) : null;
                final Object mockResponse = (mockResponses != null) ? mockResponses.get(mocker) : null;
                Answer answer = invocationOnMock -> {
                    mockRequestChecker.assertEquals(mocker, mockRequest, invocationOnMock.getArguments());
                    return mockResponse;
                };

                ongoingStubbing = ongoingStubbing.thenAnswer(answer);
            }
        } catch (Exception e) {
            Throwables.propagate(e);
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
            if (!mockObjects.isEmpty()) {
                Mockito.clearInvocations(mockObjects.toArray());
            }
        } catch (Exception e) {
            Throwables.propagate(e);
            throw new MTestException("mock cleanup error", e);
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

    /**
     *
     * @param mockRequests
     * @return
     */
    private Map<Mocker, List<Mocker>> getRequestMockers(Map<Mocker, Object[]> mockRequests) {
        if (mockRequests==null || mockRequests.isEmpty()){
            return Maps.newHashMap();
        }
        List<Mocker> requestKeys = Lists.newArrayList(mockRequests.keySet());
        requestKeys.sort((o1, o2) -> {
            if (o1.equals(o2)) return 0;
            if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
            if (o1.getMethodName().compareTo(o2.getMethodName())!=0) return o1.getMethodName().compareTo(o2.getMethodName());
            if (o1.getOverload() != o2.getOverload()) return o1.getOverload() - o2.getOverload();
            return o1.getOrder() - o2.getOrder();
        });

        Map<Mocker, List<Mocker>> requestMockers = Maps.newHashMap();
        for (Mocker mocker : requestKeys) {
            Mocker baseMocker = new Mocker();
            baseMocker.setClassSimpleName(mocker.getClassSimpleName());
            baseMocker.setMethodName(mocker.getMethodName());
            baseMocker.setOverload(mocker.getOverload());

            List<Mocker> mockers;
            if (requestMockers.containsKey(baseMocker)) {
                mockers = requestMockers.get(baseMocker);
            } else {
                mockers = Lists.newArrayList();
                requestMockers.put(baseMocker, mockers);
            }
            mockers.add(mocker);
        }
        return requestMockers;
    }

    /**
     *
     * @param mockResponses
     * @return
     */
    private Map<Mocker, List<Mocker>> getResponseMockers(Map<Mocker, Object> mockResponses) {
        if (mockResponses==null || mockResponses.isEmpty()){
            return Maps.newHashMap();
        }
        List<Mocker> responseKeys = Lists.newArrayList(mockResponses.keySet());
        responseKeys.sort((o1, o2) -> {
            if (o1.equals(o2)) return 0;
            if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
            if (o1.getMethodName().compareTo(o2.getMethodName())!=0) return o1.getMethodName().compareTo(o2.getMethodName());
            if (o1.getOverload() != o2.getOverload()) return o1.getOverload() - o2.getOverload();
            if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
            return o1.getOrder() - o2.getOrder();
        });

        Map<Mocker, List<Mocker>> responseMockers = Maps.newHashMap();
        for (Mocker mocker : responseKeys) {
            Mocker baseMocker = new Mocker();
            baseMocker.setClassSimpleName(mocker.getClassSimpleName());
            baseMocker.setMethodName(mocker.getMethodName());
            baseMocker.setOverload(mocker.getOverload());

            List<Mocker> mockers;
            if (responseMockers.containsKey(baseMocker)) {
                mockers = responseMockers.get(baseMocker);
            } else {
                mockers = Lists.newArrayList();
                responseMockers.put(baseMocker, mockers);
            }
            mockers.add(mocker);
        }
        return responseMockers;
    }
}
