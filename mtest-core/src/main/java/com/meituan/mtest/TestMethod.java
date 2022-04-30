package com.meituan.mtest;

import com.google.common.base.Objects;

import java.lang.reflect.Method;

/**
 *
 * @author Jun Tan
 */
public class TestMethod {

    private String method;

    private Class testClass;

    private String beanName;

    private int overload = -1;

    /**
     *
     * @param method
     * @param testClass
     */
    public TestMethod(String method, Class testClass) {
        this.method = method;
        this.testClass = testClass;
    }

    /**
     *
     * @param method
     * @param testClass
     * @param beanName
     */
    public TestMethod(String method, Class testClass, String beanName) {
        this.method = method;
        this.testClass = testClass;
        this.beanName = beanName;
    }

    /**
     *
     * @param method
     * @param testClass
     * @param overload
     */
    public TestMethod(String method, Class testClass, int overload) {
        this.method = method;
        this.testClass = testClass;
        this.overload = overload;
    }

    /**
     *
     * @param method
     * @param testClass
     * @param beanName
     * @param overload
     */
    public TestMethod(String method, Class testClass, String beanName, int overload) {
        this.method = method;
        this.testClass = testClass;
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
        if (! "".equals(mTest.beanName())) {
            this.beanName = mTest.beanName();
        }
        this.overload = mTest.overload();
    }

    public String getMethod() {
        return method;
    }

    public Class getTestClass() {
        return testClass;
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
        return overload == that.overload && Objects.equal(method, that.method) && Objects.equal(testClass, that.testClass) && Objects.equal(beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method, testClass, beanName, overload);
    }

    @Override
    public String toString() {
        return "method=" + method +
                ", testClass=" + testClass +
                ", beanName='" + beanName + '\'' +
                ", overload=" + overload;
    }
}
