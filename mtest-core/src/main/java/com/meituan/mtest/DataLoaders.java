package com.meituan.mtest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

public class DataLoaders {

    public static Iterable<TestCase> loadTestCases(String classSimpleName, String methodName) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + "/testcases.yaml";
        return loadTestCases(path);
    }

    public static Iterable<TestCase> loadTestCases(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + "-" + overload + "/testcases.yaml";
        return loadTestCases(path);
    }

    public static Iterable<TestCase> loadTestCases(String path) throws IOException {
        InputStream io = new ClassPathResource(path).getInputStream();
        Yaml yaml = new Yaml();
        List<Map> testcases_maps = yaml.load(io);

        List<TestCase> testcases = Lists.newArrayList();

        for (Map testcases_map : testcases_maps) {
            TestCase testcase = new TestCase();
            testcase.setCode((String) testcases_map.get("code"));
            testcase.setName((String) testcases_map.get("name"));

            testcases.add(testcase);
        }
        return testcases;
    }

    public static Iterable<Object[]> loadRequests(String classSimpleName, String methodName) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + "/requests.yaml";
        Iterable<TestCase> testCases = loadTestCases(classSimpleName, methodName);
        return loadRequests(path, testCases);
    }

    public static Iterable<Object[]> loadRequests(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + "-" + overload + "/requests.yaml";
        Iterable<TestCase> testCases = loadTestCases(classSimpleName, methodName, overload);
        return loadRequests(path, testCases);
    }

    private static Iterable<Object[]> loadRequests(String path, Iterable<TestCase> testCases) throws IOException {
        InputStream io = new ClassPathResource(path).getInputStream();
        Yaml yaml = new Yaml();
        Map requests_map = yaml.load(io);
        if (requests_map==null || requests_map.isEmpty()) {
            return Lists.newArrayList();
        }

        List<Object[]> requests = Lists.newArrayList();
        for (TestCase testCase : testCases) {
            if (requests_map.containsKey(testCase.getCode())) {
                Object[] request = ((List) requests_map.get(testCase.getCode())).toArray();
                requests.add(request);
            } else {
                requests.add(null);
            }
        }

        return requests;
    }

    public static Iterable<Object> loadResponses(String classSimpleName, String methodName) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + "/responses.yaml";
        Iterable<TestCase> testCases = loadTestCases(classSimpleName, methodName);
        return loadResponses(path, testCases);
    }

    public static Iterable<Object> loadResponses(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + "-" + overload + "/responses.yaml";
        Iterable<TestCase> testCases = loadTestCases(classSimpleName, methodName, overload);
        return loadResponses(path, testCases);
    }

    private static Iterable<Object> loadResponses(String path, Iterable<TestCase> testCases) throws IOException {
        InputStream io = new ClassPathResource(path).getInputStream();
        Yaml yaml = new Yaml();
        Map responses_map = yaml.load(io);
        if (responses_map==null || responses_map.isEmpty()) {
            return Lists.newArrayList();
        }

        List<Object> responses = Lists.newArrayList();
        for (TestCase testCase : testCases) {
            if (responses_map.containsKey(testCase.getCode())) {
                Object response = responses_map.get(testCase.getCode());
                responses.add(response);
            } else {
                responses.add(null);
            }
        }

        return responses;
    }

    public static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String classSimpleName, String methodName) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName;
        return loadAllMockRequests(path);
    }

    public static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + overload;
        return loadAllMockRequests(path);
    }

    private static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String path) throws IOException {
        File dir = new ClassPathResource(path).getFile();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("mock-") && name.endsWith("-requests.yaml");
            }
        });

        Map<String, Map<Mocker, Object[]>> allMockRequests = Maps.newHashMap();
        if (files == null || files.length == 0) {
            return allMockRequests;
        }
        for (File file : files) {
            String fileName = file.getName();
            Mocker mocker = new Mocker();
            String[] split = fileName.split("-");
            mocker.setClassSimpleName(split[1]);
            mocker.setMethodName(split[2]);
            if (split.length == 6) {
                mocker.setOverload(Integer.valueOf(split[3]));
                mocker.setOrder(Integer.valueOf(split[4]));
            }

            Map requests_map = loadMockRequests(file);
            if (requests_map==null || requests_map.isEmpty()) {
                continue;
            }

            for (Object code : requests_map.keySet()) {
                Map<Mocker, Object[]> mockRequests = allMockRequests.get(code);
                if (mockRequests == null) {
                    mockRequests = Maps.newHashMap();
                    allMockRequests.put((String) code, mockRequests);
                }
                mockRequests.put(mocker, ((List) requests_map.get(code)).toArray());
            }
        }

        return allMockRequests;
    }

    private static Map<String, List> loadMockRequests(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.load(new FileInputStream(file));
    }

    public static Map<String, Map<Mocker, Object>> loadAllMockResponses(String classSimpleName, String methodName) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName;
        return loadAllMockResponses(path);
    }

    public static Map<String, Map<Mocker, Object>> loadAllMockResponses(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtestdata/" + classSimpleName + "-" + methodName + overload;
        return loadAllMockResponses(path);
    }

    private static Map<String, Map<Mocker, Object>> loadAllMockResponses(String path) throws IOException {
        File dir = new ClassPathResource(path).getFile();
        File[] files = dir.listFiles((dir1, name) -> name.startsWith("mock-") && name.endsWith("-responses.yaml"));

        Map<String, Map<Mocker, Object>> allMockResponses = Maps.newHashMap();
        if (files == null || files.length == 0) {
            return allMockResponses;
        }
        for (File file : files) {
            String fileName = file.getName();
            Mocker mocker = new Mocker();
            String[] split = fileName.split("-");
            mocker.setClassSimpleName(split[1]);
            mocker.setMethodName(split[2]);
            if (split.length == 6) {
                mocker.setOverload(Integer.valueOf(split[3]));
                mocker.setOrder(Integer.valueOf(split[4]));
            }

            Map responses_map = loadMockResponses(file);
            if (responses_map==null || responses_map.isEmpty()) {
                continue;
            }

            for (Object code : responses_map.keySet()) {
                Map<Mocker, Object> mockRequests = allMockResponses.get(code);
                if (mockRequests == null) {
                    mockRequests = Maps.newHashMap();
                    allMockResponses.put((String) code, mockRequests);
                }
                mockRequests.put(mocker, responses_map.get(code));
            }
        }

        return allMockResponses;
    }

    private static Map<String, Object> loadMockResponses(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.load(new FileInputStream(file));
    }

}

