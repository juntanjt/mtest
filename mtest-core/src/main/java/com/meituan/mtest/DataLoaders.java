package com.meituan.mtest;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Jun Tan
 */
public class DataLoaders {

    /**
     *
     * @param testMethod
     * @return
     */
    public static Iterable<TestCase> loadTestCases(TestMethod testMethod) {
        Iterable<TestCase> testCases = loadTestCases(PathConvention.getTestCasePath(testMethod));
        if (! testCases.iterator().hasNext()) {
            return testCases;
        }
        return Lists.newArrayList(testCases).stream().filter(testCase -> ! testCase.isException()).collect(Collectors.toList());
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static Iterable<TestCase> loadExceptionTestCases(TestMethod testMethod) {
        Iterable<TestCase> testCases = loadTestCases(PathConvention.getTestCasePath(testMethod));
        if (! testCases.iterator().hasNext()) {
            return testCases;
        }
        return Lists.newArrayList(testCases).stream().filter(testCase -> testCase.isException()).collect(Collectors.toList());
    }

    /**
     *
     * @param path
     * @return
     */
    private static Iterable<TestCase> loadTestCases(String path) {
        try {
            Iterator<String[]> iterator = loadCsv(path);
            if (iterator == null) {
                throw new MTestException("load test case error, file [" + path + "] not exist");
            }

            if (iterator.hasNext()) {
                //去除第一行的表头，从第二行开始
                iterator.next();
            }
            List<TestCase> testcases = Lists.newArrayList();
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                if (next == null || next.length == 0) {
                    continue;
                }
                boolean isException = false;
                if (next.length >= 3 && "1".equals(next[2].trim()) || "ture".equals(next[2].trim())) {
                    isException = true;
                }
                if (next.length >= 4 && "1".equals(next[3].trim()) || "ture".equals(next[3].trim())) {
                    continue;
                }
                testcases.add(new TestCase(next[0].trim(), next[1].trim(), isException));
            }
            return testcases;
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load test case error. file [" + path + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCases
     * @return
     */
    public static Iterable<Object[]> loadRequests(TestMethod testMethod, Iterable<TestCase> testCases) {
        String requestPath = PathConvention.getRequestPath(testMethod);
        return loadRequests(requestPath, testCases);
    }

    /**
     *
     * @param requestPath
     * @param testCases
     * @return
     */
    private static Iterable<Object[]> loadRequests(String requestPath, Iterable<TestCase> testCases) {
        try {
            Map requestMap = loadYaml(requestPath);
            if (requestMap == null || requestMap.isEmpty()) {
                return Lists.newArrayList();
            }

            List<Object[]> requests = Lists.newArrayList();
            for (TestCase testCase : testCases) {
                if (requestMap.containsKey(testCase.getId())) {
                    Object[] request = ((List) requestMap.get(testCase.getId())).toArray();
                    requests.add(request);
                } else {
                    requests.add(null);
                }
            }

            return requests;
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load request error. file [" + requestPath + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCases
     * @return
     */
    public static Iterable<Object> loadExpecteds(TestMethod testMethod, Iterable<TestCase> testCases) {
        String expectedPath = PathConvention.getExpectedPath(testMethod);
        return loadExpecteds(expectedPath, testCases);
    }

    /**
     *
     * @param expectedPath
     * @param testCases
     * @return
     */
    private static Iterable<Object> loadExpecteds(String expectedPath, Iterable<TestCase> testCases) {
        try {
            Map expectedMap = loadYaml(expectedPath);
            if (expectedMap == null || expectedMap.isEmpty()) {
                return Lists.newArrayList();
            }

            List<Object> expecteds = Lists.newArrayList();
            for (TestCase testCase : testCases) {
                if (expectedMap.containsKey(testCase.getId())) {
                    Object expected = expectedMap.get(testCase.getId());
                    expecteds.add(expected);
                } else {
                    expecteds.add(null);
                }
            }

            return expecteds;
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load expected error. file [" + expectedPath + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCases
     * @return
     */
    public static Iterable<Throwable> loadExceptions(TestMethod testMethod, Iterable<TestCase> testCases) {
        String exceptionPath = PathConvention.getExceptionPath(testMethod);
        return loadExceptions(exceptionPath, testCases);
    }

    /**
     *
     * @param exceptionPath
     * @param testCases
     * @return
     */
    private static Iterable<Throwable> loadExceptions(String exceptionPath, Iterable<TestCase> testCases) {
        try {
            Map<String, Throwable> exceptionMap = loadYaml(exceptionPath);
            if (exceptionMap == null || exceptionMap.isEmpty()) {
                return Lists.newArrayList();
            }

            List<Throwable> exceptions = Lists.newArrayList();
            for (TestCase testCase : testCases) {
                if (exceptionMap.containsKey(testCase.getId())) {
                    Throwable expectedException = exceptionMap.get(testCase.getId());
                    exceptions.add(expectedException);
                } else {
                    exceptions.add(null);
                }
            }

            return exceptions;
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load exception error. file [" + exceptionPath + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCase
     * @return
     */
    public static Map<Mocker, List<Object[]>> loadMockRequests(TestMethod testMethod, TestCase testCase) {
        Map<String, Map<Mocker, List<Object[]>>> allMockRequests = loadAllMockRequests(testMethod);
        return allMockRequests.get(testCase.getId());
    }

    /**
     *
     * @param testMethodPath
     * @param testCase
     * @return
     */
    public static Map<Mocker, List<Object[]>> loadMockRequests(String testMethodPath, TestCase testCase) {
        Map<String, Map<Mocker, List<Object[]>>> allMockRequests = loadAllMockRequests(testMethodPath);
        return allMockRequests.get(testCase.getId());
    }

    /**
     *
     * @param testMethod
     * @return
     */
    private static Map<String, Map<Mocker, List<Object[]>>> loadAllMockRequests(TestMethod testMethod) {
        String testMethodPath = PathConvention.getTestMethodPath(testMethod);
        return loadAllMockRequests(testMethodPath);
    }

    /**
     *
     * @param testMethodPath
     * @return
     */
    private static Map<String, Map<Mocker, List<Object[]>>> loadAllMockRequests(String testMethodPath) {
        try {
            Map<String, Map<Mocker, List<Object[]>>> allMockRequests = Maps.newHashMap();

            Resource dir = new ClassPathResource(PathConvention.getMockFileDir(testMethodPath));
            if (! dir.exists() || ! dir.getFile().isDirectory()) {
                return allMockRequests;
            }

            File[] files = PathConvention.getMockRequestFiles(dir.getFile());
            if (files == null || files.length == 0) {
                return allMockRequests;
            }
            for (File file : files) {
                Mocker mocker = PathConvention.getMocker(file.getName());

                Map<String, List> requestMap = loadYaml(file);
                if (requestMap == null || requestMap.isEmpty()) {
                    continue;
                }

                Map<String/** code*/, Map<Integer/** order */, Object[]>> requestCodeOrderMap = Maps.newHashMap();

                for (String key : requestMap.keySet()) {
                    List<?> request = requestMap.get(key);

                    // code split by ","
                    String[] codes = key.split(",");
                    if (codes == null || codes.length==0) {
                        continue;
                    }
                    for (String code : codes) {
                        code = code.trim();
                        if (Strings.isNullOrEmpty(code)) {
                            continue;
                        }
                        // example: code1(0), code1(1)
                        String[] codeInfo = code.split("\\(|\\)");
                        Map<Integer/** order */, Object[]> requestOrderMap = requestCodeOrderMap.get(codeInfo[0].trim());
                        if (requestOrderMap == null) {
                            requestOrderMap = Maps.newHashMap();
                            requestCodeOrderMap.put(codeInfo[0].trim(), requestOrderMap);
                        }
                        if (codeInfo.length == 1) {
                            requestOrderMap.put(0, request.toArray());
                        } else {
                            requestOrderMap.put(Integer.valueOf(codeInfo[1].trim()), request.toArray());
                        }
                    }
                }
                for (String code : requestCodeOrderMap.keySet()) {
                    Map<Mocker, List<Object[]>> mockRequests = allMockRequests.get(code);
                    if (mockRequests == null) {
                        mockRequests = Maps.newHashMap();
                        allMockRequests.put(code, mockRequests);
                    }
                    List<Object[]> requests = Lists.newArrayList();
                    for (int i=0; ; i++) {
                        if (requestCodeOrderMap.get(code).containsKey(i)) {
                            requests.add(requestCodeOrderMap.get(code).get(i));
                        } else {
                            break;
                        }
                    }
                    mockRequests.put(mocker, requests);
                }
            }

            return allMockRequests;
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load mock request error. dir [" + testMethodPath + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCase
     * @return
     */
    public static Map<Mocker, List<Object>> loadMockResponses(TestMethod testMethod, TestCase testCase) {
        Map<String, Map<Mocker, List<Object>>> allMockResponses = loadAllMockResponses(testMethod);
        return allMockResponses.get(testCase.getId());
    }

    /**
     *
     * @param testMethodPath
     * @param testCase
     * @return
     */
    public static Map<Mocker, List<Object>> loadMockResponses(String testMethodPath, TestCase testCase) {
        Map<String, Map<Mocker, List<Object>>> allMockResponses = loadAllMockResponses(testMethodPath);
        return allMockResponses.get(testCase.getId());
    }

    /**
     *
     * @param testMethod
     * @return
     */
    private static Map<String, Map<Mocker, List<Object>>> loadAllMockResponses(TestMethod testMethod) {
        String testMethodPath = PathConvention.getTestMethodPath(testMethod);
        return loadAllMockResponses(testMethodPath);
    }

    /**
     *
     * @param testMethodPath
     * @return
     */
    private static Map<String, Map<Mocker, List<Object>>> loadAllMockResponses(String testMethodPath) {
        try {
            Map<String, Map<Mocker, List<Object>>> allMockResponses = Maps.newHashMap();

            Resource dir = new ClassPathResource(PathConvention.getMockFileDir(testMethodPath));
            if (! dir.exists() || ! dir.getFile().isDirectory()) {
                return allMockResponses;
            }

            File[] files = PathConvention.getMockResponseFiles(dir.getFile());
            if (files == null || files.length == 0) {
                return allMockResponses;
            }
            for (File file : files) {
                Mocker mocker = PathConvention.getMocker(file.getName());

                Map<String, Object> responseMap = loadYaml(file);
                if (responseMap == null || responseMap.isEmpty()) {
                    continue;
                }

                Map<String/** code*/, Map<Integer/** order */, Object>> responseCodeOrderMap = Maps.newHashMap();

                for (String key : responseMap.keySet()) {
                    Object response = responseMap.get(key);

                    // code split by ","
                    String[] codes = key.split(",");
                    if (codes == null || codes.length==0) {
                        continue;
                    }
                    for (String code : codes) {
                        code = code.trim();
                        if (Strings.isNullOrEmpty(code)) {
                            continue;
                        }
                        // example: code1(0), code1(1)
                        String[] codeInfo = code.split("\\(|\\)");
                        Map<Integer/** order */, Object> responseOrderMap = responseCodeOrderMap.get(codeInfo[0].trim());
                        if (responseOrderMap == null) {
                            responseOrderMap = Maps.newHashMap();
                            responseCodeOrderMap.put(codeInfo[0].trim(), responseOrderMap);
                        }
                        if (codes.length == 1) {
                            responseOrderMap.put(0, response);
                        } else {
                            responseOrderMap.put(Integer.valueOf(codeInfo[1].trim()), response);
                        }
                    }
                }
                for (String code : responseCodeOrderMap.keySet()) {
                    Map<Mocker, List<Object>> mockResponses = allMockResponses.get(code);
                    if (mockResponses == null) {
                        mockResponses = Maps.newHashMap();
                        allMockResponses.put(code, mockResponses);
                    }
                    List<Object> responses = Lists.newArrayList();
                    for (int i=0; ; i++) {
                        if (responseCodeOrderMap.get(code).containsKey(i)) {
                            responses.add(responseCodeOrderMap.get(code).get(i));
                        } else {
                            break;
                        }
                    }
                    mockResponses.put(mocker, responses);
                }
            }

            return allMockResponses;
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load mock response error. dir [" + testMethodPath + "]", e);
        }
    }

    /**
     *
     * @param path
     * @return
     */
    private static Iterator<String[]> loadCsv(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            if (! resource.exists()) {
                return null;
            }
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(resource.getInputStream())).build();
            return csvReader.iterator();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     *
     * @param file
     * @return
     */
    private static Iterator<String[]> loadCsv(File file) {
        try {
            if (! file.exists()) {
                return null;
            }
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file))).build();
            return csvReader.iterator();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     *
     * @param path
     * @param <T>
     * @return
     */
    private static <T> T loadYaml(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            if (! resource.exists()) {
                return null;
            }
            Yaml yaml = new Yaml();
            return yaml.load(resource.getInputStream());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     *
     * @param file
     * @param <T>
     * @return
     */
    private static <T> T loadYaml(File file) {
        try {
            if (! file.exists()) {
                return null;
            }
            Yaml yaml = new Yaml();
            return yaml.load(new FileInputStream(file));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}

