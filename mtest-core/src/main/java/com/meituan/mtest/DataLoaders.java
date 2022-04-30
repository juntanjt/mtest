package com.meituan.mtest;

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
        return loadTestCases(PathConvention.getTestCasePath(testMethod));
    }

    /**
     *
     * @param path
     * @return
     */
    public static Iterable<TestCase> loadTestCases(String path) {
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
                TestCase testcase = new TestCase();
                testcase.setId(next[0]);
                testcase.setName(next[1]);
                if (next.length >= 3 && "1".equals(next[2]) || "ture".equals(next[2])) {
                    continue;
                }

                testcases.add(testcase);
            }
            return MTestContext.ContextIterable.of(testcases, MTestContext.KeyType.TEST_CASE);
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load test case error. file [" + path + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static Iterable<Object[]> loadRequests(TestMethod testMethod) {
        String testCasePath = PathConvention.getTestCasePath(testMethod);
        String requestPath = PathConvention.getRequestPath(testMethod);
        return loadRequests(testCasePath, requestPath);
    }

    /**
     *
     * @param testCasePath
     * @param requestPath
     * @return
     */
    private static Iterable<Object[]> loadRequests(String testCasePath, String requestPath) {
        try {
            Iterable<TestCase> testCases = loadTestCases(testCasePath);

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

            return MTestContext.ContextIterable.of(requests, MTestContext.KeyType.REQUEST);
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load request error. file [" + requestPath + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static Iterable<Object> loadExpecteds(TestMethod testMethod) {
        String testCasePath = PathConvention.getTestCasePath(testMethod);
        String expectedPath = PathConvention.getExpectedPath(testMethod);
        return loadExpecteds(testCasePath, expectedPath);
    }

    /**
     *
     * @param testCasePath
     * @param expectedPath
     * @return
     */
    private static Iterable<Object> loadExpecteds(String testCasePath, String expectedPath) {
        try {
            Iterable<TestCase> testCases = loadTestCases(testCasePath);

            Map expectedMap = loadYaml(expectedPath);
            if (expectedMap == null || expectedMap.isEmpty()) {
                return Lists.newArrayList();
            }

            List<Object> expecteds = Lists.newArrayList();
            for (TestCase testCase : testCases) {
                if (expectedMap.containsKey(testCase.getId())) {
                    Object response = expectedMap.get(testCase.getId());
                    expecteds.add(response);
                } else {
                    expecteds.add(null);
                }
            }

            return MTestContext.ContextIterable.of(expecteds, MTestContext.KeyType.EXPECTED);
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("load expected error. file [" + expectedPath + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCase
     * @return
     */
    public static Map<Mocker, Object[]> loadMockRequests(TestMethod testMethod, TestCase testCase) {
        Map<String, Map<Mocker, Object[]>> allMockRequests = loadAllMockRequests(testMethod);
        return allMockRequests.get(testCase.getId());
    }

    /**
     *
     * @param testMethodPath
     * @param testCase
     * @return
     */
    public static Map<Mocker, Object[]> loadMockRequests(String testMethodPath, TestCase testCase) {
        Map<String, Map<Mocker, Object[]>> allMockRequests = loadAllMockRequests(testMethodPath);
        return allMockRequests.get(testCase.getId());
    }

    /**
     *
     * @param testMethod
     * @return
     */
    private static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(TestMethod testMethod) {
        String testMethodPath = PathConvention.getTestMethodPath(testMethod);
        return loadAllMockRequests(testMethodPath);
    }

    /**
     *
     * @param testMethodPath
     * @return
     */
    private static Map<String, Map<Mocker, Object[]>> loadAllMockRequests(String testMethodPath) {
        try {
            Map<String, Map<Mocker, Object[]>> allMockRequests = Maps.newHashMap();

            Resource dir = new ClassPathResource(testMethodPath);
            if (! dir.exists() || ! dir.getFile().isDirectory()) {
                return allMockRequests;
            }

            File[] files = PathConvention.getMockRequestFiles(dir.getFile());
            if (files == null || files.length == 0) {
                return allMockRequests;
            }
            for (File file : files) {
                Mocker mocker = PathConvention.getMocker(file.getName());

                Map requestMap = loadYaml(file);
                if (requestMap == null || requestMap.isEmpty()) {
                    continue;
                }

                for (Object code : requestMap.keySet()) {
                    Map<Mocker, Object[]> mockRequests = allMockRequests.get(code);
                    if (mockRequests == null) {
                        mockRequests = Maps.newHashMap();
                        allMockRequests.put((String) code, mockRequests);
                    }
                    mockRequests.put(mocker, ((List) requestMap.get(code)).toArray());
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
    public static Map<Mocker, Object> loadMockResponses(TestMethod testMethod, TestCase testCase) {
        Map<String, Map<Mocker, Object>> allMockResponses = loadAllMockResponses(testMethod);
        return allMockResponses.get(testCase.getId());
    }

    /**
     *
     * @param testMethodPath
     * @param testCase
     * @return
     */
    public static Map<Mocker, Object> loadMockResponses(String testMethodPath, TestCase testCase) {
        Map<String, Map<Mocker, Object>> allMockResponses = loadAllMockResponses(testMethodPath);
        return allMockResponses.get(testCase.getId());
    }

    /**
     *
     * @param testMethod
     * @return
     */
    private static Map<String, Map<Mocker, Object>> loadAllMockResponses(TestMethod testMethod) {
        String testMethodPath = PathConvention.getTestMethodPath(testMethod);
        return loadAllMockResponses(testMethodPath);
    }

    /**
     *
     * @param testMethodPath
     * @return
     */
    private static Map<String, Map<Mocker, Object>> loadAllMockResponses(String testMethodPath) {
        try {
            Map<String, Map<Mocker, Object>> allMockResponses = Maps.newHashMap();

            Resource dir = new ClassPathResource(testMethodPath);
            if (! dir.exists() || ! dir.getFile().isDirectory()) {
                return allMockResponses;
            }

            File[] files = PathConvention.getMockResponseFiles(dir.getFile());
            if (files == null || files.length == 0) {
                return allMockResponses;
            }
            for (File file : files) {
                Mocker mocker = PathConvention.getMocker(file.getName());

                Map responseMap = loadYaml(file);
                if (responseMap == null || responseMap.isEmpty()) {
                    continue;
                }

                for (Object code : responseMap.keySet()) {
                    Map<Mocker, Object> mockResponses = allMockResponses.get(code);
                    if (mockResponses == null) {
                        mockResponses = Maps.newHashMap();
                        allMockResponses.put((String) code, mockResponses);
                    }
                    mockResponses.put(mocker, responseMap.get(code));
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

