package com.meituan.mtest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class MockMakers {

    public static void mock(TestCase testCase, String classSimpleName, String methodName, TestMethod[] mockMethods, Map<String, Object> mockObjects) throws IOException, InvocationTargetException, IllegalAccessException {
        Map<String, Map<Mocker, Object[]>> allMockRequests = DataLoaders.loadAllMockRequests(classSimpleName, methodName);
        Map<String, Map<Mocker, Object>> allMockResponses = DataLoaders.loadAllMockResponses(classSimpleName, methodName);
        mock(mockMethods, mockObjects, allMockRequests.get(testCase.getId()), allMockResponses.get(testCase.getId()));
    }

    public static void mock(TestCase testCase, String classSimpleName, String methodName, int overload, TestMethod[] mockMethods, Map<String, Object> mockObjects) throws IOException, InvocationTargetException, IllegalAccessException {
        Map<String, Map<Mocker, Object[]>> allMockRequests = DataLoaders.loadAllMockRequests(classSimpleName, methodName, overload);
        Map<String, Map<Mocker, Object>> allMockResponses = DataLoaders.loadAllMockResponses(classSimpleName, methodName, overload);
        mock(mockMethods, mockObjects, allMockRequests.get(testCase.getId()), allMockResponses.get(testCase.getId()));
    }

    private static void mock(TestMethod[] mockMethods, Map<String, Object> mockObjects, Map<Mocker, Object[]> mockRequests, Map<Mocker, Object> mockResponses) throws InvocationTargetException, IllegalAccessException {

        Map<Mocker, List<Mocker>> requestMockersMap = getRequestMockers(mockRequests);
        Map<Mocker, List<Mocker>> responseMockersMap = getResponseMockers(mockResponses);

        for (TestMethod mockMethod : mockMethods) {
            Object mockObject = mockObjects.get(mockMethod.getBeanName());

            Mocker baseMocker = new Mocker();
            baseMocker.setClassSimpleName(mockMethod.getTestClass().getSimpleName());
            baseMocker.setMethodName(mockMethod.getMethod().getName());
            baseMocker.setOverload(mockMethod.getOverload());

//            List<Mocker> requestMockers = requestMockersMap.get(baseMocker);
            List<Mocker> responseMockers = responseMockersMap.get(baseMocker);

            // TODO request

            List<Object> args = Lists.newArrayList();
            for (Class parameterType : mockMethod.getMethod().getParameterTypes()) {
                args.add(Mockito.any(parameterType));
            }

            OngoingStubbing ongoingStubbing = Mockito.when(mockMethod.getMethod().invoke(mockObject, args.toArray()));
            for (Mocker responseMocker : responseMockers) {
                ongoingStubbing = ongoingStubbing.thenReturn(mockResponses.get(responseMocker));
            }
        }

    }

    private static Map<Mocker, List<Mocker>> getRequestMockers(Map<Mocker, Object[]> mockRequests) {
        if (mockRequests==null || mockRequests.isEmpty()){
            return Maps.newHashMap();
        }
        List<Mocker> requestKeySet = Lists.newArrayList(mockRequests.keySet());
        requestKeySet.sort((o1, o2) -> {
            if (o1.equals(o2)) return 0;
            if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
            if (o1.getMethodName().compareTo(o2.getMethodName())!=0) return o1.getMethodName().compareTo(o2.getMethodName());
            if (o1.getOverload() != o2.getOverload()) return o1.getOverload() - o2.getOverload();
            return o1.getOrder() - o2.getOrder();
        });

        Map<Mocker, List<Mocker>> requestMockers = Maps.newHashMap();
        for (Mocker mocker : requestKeySet) {
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

    private static Map<Mocker, List<Mocker>> getResponseMockers(Map<Mocker, Object> mockResponses) {
        if (mockResponses==null || mockResponses.isEmpty()){
            return Maps.newHashMap();
        }
        List<Mocker> responseKeySet = Lists.newArrayList(mockResponses.keySet());
        responseKeySet.sort((o1, o2) -> {
            if (o1.equals(o2)) return 0;
            if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
            if (o1.getMethodName().compareTo(o2.getMethodName())!=0) return o1.getMethodName().compareTo(o2.getMethodName());
            if (o1.getOverload() != o2.getOverload()) return o1.getOverload() - o2.getOverload();
            if (o1.getClassSimpleName().compareTo(o2.getClassSimpleName())!=0) return o1.getClassSimpleName().compareTo(o2.getClassSimpleName());
            return o1.getOrder() - o2.getOrder();
        });

        Map<Mocker, List<Mocker>> responseMockers = Maps.newHashMap();
        for (Mocker mocker : responseKeySet) {
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
