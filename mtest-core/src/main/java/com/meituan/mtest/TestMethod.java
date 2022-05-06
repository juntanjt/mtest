package com.meituan.mtest;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.StringJoiner;

/**
 *
 * @author Jun Tan
 */
public class TestMethod {

    private Class<?> testClass;

    private String method;

    private String beanName;

    private int overload = -1;

    /**
     *
     * @param testClass
     * @param method
     */
    public TestMethod(Class<?> testClass, String method) {
        this.testClass = testClass;
        this.method = method;
    }

    /**
     *
     * @param testClass
     * @param method
     * @param beanName
     */
    public TestMethod(Class<?> testClass, String method, String beanName) {
        this.testClass = testClass;
        this.method = method;
        this.beanName = beanName;
    }

    /**
     *
     * @param testClass
     * @param method
     * @param overload
     */
    public TestMethod(Class<?> testClass, String method, int overload) {
        this.testClass = testClass;
        this.method = method;
        this.overload = overload;
    }

    /**
     *
     * @param testClass
     * @param method
     * @param beanName
     * @param overload
     */
    public TestMethod(Class<?> testClass, String method, String beanName, int overload) {
        this.testClass = testClass;
        this.method = method;
        this.beanName = beanName;
        this.overload = overload;
    }

    /**
     *
     * @param mTest
     */
    public TestMethod(MTest mTest) {
        this.method = mTest.method();
        this.testClass = mTest.testClass();
        if (! Strings.isNullOrEmpty(mTest.beanName())) {
            this.beanName = mTest.beanName();
        }
        this.overload = mTest.overload();
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public String getMethod() {
        return method;
    }

    public String getBeanName() {
        return beanName;
    }

    public int getOverload() {
        return overload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestMethod that = (TestMethod) o;
        return overload == that.overload && Objects.equal(testClass, that.testClass) && Objects.equal(method, that.method) && Objects.equal(beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(testClass, method, beanName, overload);
    }

    @Override
    public String toString() {
        return new StringJoiner("")
                .add("testClass=" + testClass)
                .add("method='" + method + "'")
                .add("beanName='" + beanName + "'")
                .add("overload=" + overload)
                .toString();
    }
}
