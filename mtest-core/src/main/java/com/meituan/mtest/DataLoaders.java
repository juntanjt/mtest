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
            List<String[]> list = loadCsv(path);
            if (list == null || list.isEmpty()) {
                throw new MTestException("load test case error, file [" + path + "] not exist");
            }

            List<TestCase> testcases = Lists.newArrayList();
            //去除第一行的表头，从第二行开始
            for (int i=1; i<list.size(); i++) {
                String[] next = list.get(i);
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
            Map<String, List> yamlMap = loadYaml(requestPath);
            Map<String, List> requestMap = splitCaseCodeKey(yamlMap);
            if (requestMap == null || requestMap.isEmpty()) {
                return Lists.newArrayList();
            }

            List<Object[]> requests = Lists.newArrayList();
            for (TestCase testCase : testCases) {
                if (requestMap.containsKey(testCase.getId())) {
                    Object[] request = (requestMap.get(testCase.getId())).toArray();
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
            Map<String, Object> yamlMap = loadYaml(expectedPath);
            Map<String, Object> expectedMap = splitCaseCodeKey(yamlMap);
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
            Map<String, Throwable> yamlMap = loadYaml(exceptionPath);
            Map<String, Throwable> exceptionMap = splitCaseCodeKey(yamlMap);
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

                Map<String, List> yamlMap = loadYaml(file);
                Map<String/** code*/, Map<Integer/** order */, List>> requestCodeOrderMap = splitMockCaseCodeKey(yamlMap);
                if (requestCodeOrderMap == null || requestCodeOrderMap.isEmpty()) {
                    continue;
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
                            requests.add(requestCodeOrderMap.get(code).get(i).toArray());
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

                Map<String, Object> yamlMap = loadYaml(file);
                Map<String/** code*/, Map<Integer/** order */, Object>> responseCodeOrderMap = splitMockCaseCodeKey(yamlMap);
                if (responseCodeOrderMap == null || responseCodeOrderMap.isEmpty()) {
                    continue;
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
     * @param map
     * @param <T>
     * @return Map<String(caseCode), T>
     */
    private static <T> Map<String, T> splitCaseCodeKey(Map<String, T> map) {
        if (map == null || map.isEmpty()) {
            return Maps.newHashMap();
        }

        Map<String, T> result = Maps.newHashMap();
        for (String key : map.keySet()) {
            List<String> caseCodes = splitCaseCodes(key);
            if (caseCodes == null || caseCodes.isEmpty()) {
                continue;
            }
            for (String caseCode : caseCodes) {
                if (Strings.isNullOrEmpty(caseCode)) {
                    continue;
                }
                result.put(caseCode, map.get(key));
            }
        }
        return result;
    }

    /**
     *
     * @param map
     * @param <T>
     * @return Map<String(caseCode), Map<Integer(order), T>>
     */
    private static <T> Map<String, Map<Integer, T>> splitMockCaseCodeKey(Map<String, T> map) {
        if (map == null || map.isEmpty()) {
            return Maps.newHashMap();
        }
        Map<String/** code*/, Map<Integer/** order */, T>> resultMap = Maps.newHashMap();

        for (String key : map.keySet()) {
            // code split by ","
            List<String[]> codeInfos = splitMockCaseCodes(key);
            if (codeInfos == null || codeInfos.size()==0) {
                continue;
            }
            for (String[] codeInfo : codeInfos) {
                Map<Integer/** order */, T> orderMap = resultMap.get(codeInfo[0]);
                if (orderMap == null) {
                    orderMap = Maps.newHashMap();
                    resultMap.put(codeInfo[0], orderMap);
                }
                if (codeInfo.length == 1) {
                    orderMap.put(0, map.get(key));
                } else {
                    orderMap.put(Integer.valueOf(codeInfo[1]), map.get(key));
                }
            }
        }
        return resultMap;
    }

    /**
     *
     * @param key
     * @return
     */
    private static List<String> splitCaseCodes(String key) {
        // code split by ","
        String[] codeStrs = key.split(",");
        if (codeStrs == null || codeStrs.length==0) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(codeStrs).stream().map(s -> s.trim()).collect(Collectors.toList());
    }

    /**
     *
     * @param key
     * @return
     */
    private static List<String[]> splitMockCaseCodes(String key) {
        // code split by ","
        String[] codeStrs = key.split(",");
        if (codeStrs == null || codeStrs.length==0) {
            return Lists.newArrayList();
        }
        List<String[]> caseCodes = Lists.newArrayList();
        for (String codeStr : codeStrs) {
            codeStr = codeStr.trim();
            if (Strings.isNullOrEmpty(codeStr)) {
                continue;
            }
            // example: code1(0), code1(1)
            String[] codeInfo = codeStr.split("\\(|\\)");
            if (codeInfo == null || codeInfo.length == 0) {
                continue;
            } else if (codeInfo.length == 1) {
                caseCodes.add(new String[] {codeInfo[0].trim()});
            } else {
                caseCodes.add(new String[] {codeInfo[0].trim(), codeInfo[1].trim()});
            }
        }
        return caseCodes;
    }

    /**
     *
     * @param path
     * @return
     */
    private static List<String[]> loadCsv(String path) {
        CSVReader csvReader = null;
        try {
            Resource resource = new ClassPathResource(path);
            if (! resource.exists()) {
                return null;
            }
            csvReader = new CSVReaderBuilder(new InputStreamReader(resource.getInputStream())).build();
            return csvReader.readAll();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    /**
     *
     * @param path
     * @param <T>
     * @return
     */
    private static <T> T loadYaml(String path) {
        InputStream inputStream = null;
        try {
            Resource resource = new ClassPathResource(path);
            if (! resource.exists()) {
                return null;
            }
            inputStream = resource.getInputStream();
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

    /**
     *
     * @param file
     * @param <T>
     * @return
     */
    private static <T> T loadYaml(File file) {
        InputStream inputStream = null;
        try {
            if (! file.exists()) {
                return null;
            }
            inputStream = new FileInputStream(file);
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    throw Throwables.propagate(e);
                }
            }
        }
    }

}

