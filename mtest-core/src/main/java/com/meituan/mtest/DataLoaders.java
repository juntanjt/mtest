package com.meituan.mtest;

import com.google.common.collect.Lists;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class DataLoaders {

    public static TestCase[] loadTestCases(String classSimpleName, String methodName) throws IOException {
        String path = "mtestdata/" + classSimpleName + "_" + methodName + "/testcases.yaml";
        return loadTestCases(path);
    }

    public static TestCase[] loadTestCases(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtestdata/" + classSimpleName + "_" + methodName + "_" + overload + "/testcases.yaml";
        return loadTestCases(path);
    }

    public static TestCase[] loadTestCases(String path) throws IOException {
        InputStream io = new ClassPathResource(path).getInputStream();
        Yaml yaml = new Yaml();
        List<Map> testcases_maps = yaml.load(io);

        List<TestCase> testcases = Lists.newArrayList();

        for (Map testcases_map : testcases_maps) {
            TestCase testcase = new TestCase();
            testcase.setCode((String) testcases_map.get("code"));
            testcase.setName((String) testcases_map.get("name"));
            List<TestCase.Mock> mocks = Lists.newArrayList();

            if (testcases_map.containsKey("mocks")) {
                for (Map mock_map : (List<Map>) testcases_map.get("mocks")) {
                    TestCase.Mock mock = new TestCase.Mock();
                    mock.setClassName((String) mock_map.get("className"));
                    mock.setBeanName((String) mock_map.get("beanName"));
                    mock.setMethodName((String) mock_map.get("methodName"));
                    if (mock_map.containsKey("methodParameterTypes")) {
                        List<String> methodParameterTypes = (List<String>) mock_map.get("methodParameterTypes");
                        mock.setMethodParameterTypes(methodParameterTypes.toArray(new String[0]));
                    }
                    if (mock_map.containsKey("order")) {
                        mock.setOrder((Integer) mock_map.get("order"));
                    }

                    mocks.add(mock);
                }
            }
            testcase.setMocks(mocks.toArray(new TestCase.Mock[0]));
            testcases.add(testcase);
        }
        return testcases.toArray(new TestCase[0]);
    }

    public static Map[] loadRequests(String classSimpleName, String methodName, TestCase[] testCases) throws IOException {
        String path = "mtestdata/" + classSimpleName + "_" + methodName + "/requests.yaml";
        return loadRequests(path, testCases);
    }

    public static Map[] loadRequests(String classSimpleName, String methodName, int overload, TestCase[] testCases) throws IOException {
        String path = "mtestdata/" + classSimpleName + "_" + methodName + "_" + overload + "/requests.yaml";
        return loadRequests(path, testCases);
    }

    public static Map[] loadRequests(String path, TestCase[] testCases) throws IOException {
        InputStream io = new ClassPathResource(path).getInputStream();
        Yaml yaml = new Yaml();
        Map requests_map = yaml.load(io);

        List<Map> requests = Lists.newArrayList();
        for (TestCase testCase : testCases) {
            if (requests_map.containsKey(testCase.getMocks())) {
                Map request = (Map) requests_map.get(testCase.getCode());
                requests.add(request);
            } else {
                requests.add(null);
            }
        }

        return requests.toArray(new Map[0]);
    }

    public static Map[][] loadAllMockRequests(String className, String methodName, TestCase[] testCases) throws IOException {
        String path = "mtestdata/" + className + "_" + methodName;
        return loadAllMockRequests(path, testCases);
    }

    public static Map[][] loadAllMockRequests(String className, String methodName, int overload, TestCase[] testCases) throws IOException {
        String path = "mtestdata/" + className + "_" + methodName + overload;
        return loadAllMockRequests(path, testCases);
    }

    public static Map[][] loadAllMockRequests(String path, TestCase[] testCases) throws IOException {
        List<Map[]> allMockRequests = Lists.newArrayList();
        for (TestCase testCase : testCases) {
            Map[] mockRequests = loadMockRequests(path, testCase);
            allMockRequests.add(mockRequests);
        }
        return allMockRequests.toArray(new Map[0][]);
    }

    public static Map[] loadMockRequests(String path, TestCase testCase) throws IOException {
//        path = path + "mock_" + Class.forName(testCase.g);
//        return loadRequests(path, testCases);
        return null;
    }

    public static Map[] loadMockRequests(String path, TestCase.Mock mock) throws IOException {
        return null;
    }

}

