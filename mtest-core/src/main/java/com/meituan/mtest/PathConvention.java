package com.meituan.mtest;

import com.google.common.base.Strings;

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
        String location = ! Strings.isNullOrEmpty(testMethod.getLocation()) ? testMethod.getLocation().trim() : "mtest-data";
        String path = location + "/" + ClassSimpleNameUtil.getSimpleName(testMethod.getTestClass()) + "-" + testMethod.getMethod();
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
    public static String getExceptionPath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/exception.yaml";
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
     * @param testMethod
     * @return
     */
    public static String getExpectedExceptionPath(TestMethod testMethod) {
        return getTestMethodPath(testMethod) + "/expectedException.yaml";
    }

    /**
     *
     * @param dir
     * @return
     */
    public static String getMockFileDir(String dir) {
        return dir + "/mock";
    }

    /**
     *
     * @param dir
     * @return
     */
    public static File[] getMockRequestFiles(File dir) {
        return dir.listFiles((dir1, name) -> name.endsWith("-request.yaml"));
    }

    /**
     *
     * @param dir
     * @return
     */
    public static File[] getMockResponseFiles(File dir) {
        return dir.listFiles((dir1, name) -> name.endsWith("-response.yaml"));
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static Mocker getMocker(String fileName) {
        String[] split = fileName.split("-");
        if (split.length <= 3) {
            return new Mocker(split[0], split[1]);
        } else {
            return new Mocker(split[0], split[1], Integer.valueOf(split[3]));
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
