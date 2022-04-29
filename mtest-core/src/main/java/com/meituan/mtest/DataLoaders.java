package com.meituan.mtest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataLoaders {

    public static Iterable<TestCase> loadTestCases(String classSimpleName, String methodName) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + "/testcase.csv";
        return loadTestCases(path);
    }

    public static Iterable<TestCase> loadTestCases(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + "-" + overload + "/testcase.csv";
        return loadTestCases(path);
    }

    public static Iterable<TestCase> loadTestCases(String path) throws IOException {
        InputStream io = new ClassPathResource(path).getInputStream();
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(io)).build();

        List<TestCase> testcases = Lists.newArrayList();

        Iterator<String[]> iterator = csvReader.iterator();
        if (iterator.hasNext()) {
            //去除第一行的表头，从第二行开始
            iterator.next();
        }
        while (iterator.hasNext()) {
            String[] next = iterator.next();
            if (next==null || next.length==0) {
                continue;
            }
            TestCase testcase = new TestCase();
            testcase.setId(next[0]);
            testcase.setName(next[1]);
            if (next.length>=3 && (next[2].equals("1") || next[2].equals("ture"))) {
                continue;
            }

            testcases.add(testcase);
        }
        return MtestContext.ContextIterable.of(testcases, MtestContext.KeyType.TEST_CASE);
    }

    public static Iterable<Object[]> loadRequests(String classSimpleName, String methodName) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + "/request.yaml";
        Iterable<TestCase> testCases = loadTestCases(classSimpleName, methodName);
        return loadRequests(path, testCases);
    }

    public static Iterable<Object[]> loadRequests(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + "-" + overload + "/request.yaml";
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
            if (requests_map.containsKey(testCase.getId())) {
                Object[] request = ((List) requests_map.get(testCase.getId())).toArray();
                requests.add(request);
            } else {
                requests.add(null);
            }
        }

        return MtestContext.ContextIterable.of(requests, MtestContext.KeyType.REQUEST);
    }

    public static Iterable<Object> loadResponses(String classSimpleName, String methodName) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + "/expected.yaml";
        Iterable<TestCase> testCases = loadTestCases(classSimpleName, methodName);
        return loadResponses(path, testCases);
    }

    public static Iterable<Object> loadResponses(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + "-" + overload + "/expected.yaml";
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
            if (responses_map.containsKey(testCase.getId())) {
                Object response = responses_map.get(testCase.getId());
                responses.add(response);
            } else {
                responses.add(null);
            }
        }

        return MtestContext.ContextIterable.of(responses, MtestContext.KeyType.EXPECTED);
    }

    public static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String classSimpleName, String methodName) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName;
        return loadAllMockRequests(path);
    }

    public static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + overload;
        return loadAllMockRequests(path);
    }

    private static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String path) throws IOException {
        File dir = new ClassPathResource(path).getFile();
        File[] files = dir.listFiles((dir1, name) -> name.startsWith("mock-") && name.endsWith("-request.yaml"));

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
            if (split.length >= 5) {
                mocker.setOverload(Integer.valueOf(split[3]));
            }
            if (split.length >= 6) {
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
        String path = "mtest-data/" + classSimpleName + "-" + methodName;
        return loadAllMockResponses(path);
    }

    public static Map<String, Map<Mocker, Object>> loadAllMockResponses(String classSimpleName, String methodName, int overload) throws IOException {
        String path = "mtest-data/" + classSimpleName + "-" + methodName + overload;
        return loadAllMockResponses(path);
    }

    private static Map<String, Map<Mocker, Object>> loadAllMockResponses(String path) throws IOException {
        File dir = new ClassPathResource(path).getFile();
        File[] files = dir.listFiles((dir1, name) -> name.startsWith("mock-") && name.endsWith("-response.yaml"));

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

