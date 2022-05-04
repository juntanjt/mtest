package com.meituan.mtest;

import java.io.File;

/**
 *
 * @author Jun Tan
 */
public class PathConvention {

    /**
     *
     * @param testMethod
     * @return
     */
    public static String getTestMethodPath(TestMethod testMethod) {
        String path = "mtest-data/" + testMethod.getTestClass().getSimpleName() + "-" + testMethod.getMethod();
        if (testMethod.getOverload() != -1) {
            path += ("-" + testMethod.getOverload());
        }
        return path;
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static String getTestCasePath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/testcase.csv";
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static String getRequestPath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/request.yaml";
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static String getExpectedPath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/expected.yaml";
    }

    /**
     *
     * @param dir
     * @return
     */
    public static File[] getMockRequestFiles(File dir) {
        return dir.listFiles((dir1, name) -> name.startsWith("mock-") && name.endsWith("-request.yaml"));
    }

    /**
     *
     * @param dir
     * @return
     */
    public static File[] getMockResponseFiles(File dir) {
        return dir.listFiles((dir1, name) -> name.startsWith("mock-") && name.endsWith("-response.yaml"));
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static Mocker getMocker(String fileName) {
        String[] split = fileName.split("-");
        if (split.length <= 4) {
            return new Mocker(split[1], split[2]);
        } else {
            return new Mocker(split[1], split[2], Integer.valueOf(split[3]));
        }
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static String getTestMethodDBSetUpPath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/db-data-setUp.xml";
    }

    /**
     *
     * @param testMethod
     * @param testCase
     * @return
     */
    public static String getTestCaseDBSetUpPath(TestMethod testMethod, TestCase testCase) {
        return getTestMethodPath(testMethod) + "/db-data/" + testCase.getId() + "-setUp.xml";
    }

    /**
     *
     * @param testMethod
     * @return
     */
    public static String getTestMethodDBExpectedPath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/db-data-expected.xml";
    }

    /**
     *
     * @param testMethod
     * @param testCase
     * @return
     */
    public static String getTestCaseDBExpectedPath(TestMethod testMethod, TestCase testCase) {
        return getTestMethodPath(testMethod) + "/db-data/" + testCase.getId() + "-expected.xml";
    }

}
