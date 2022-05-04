package com.meituan.mtest;

import com.google.common.base.Objects;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 *
 * @author Jun Tan
 */
public class MockMethod {

    private Class testClass;

    private Method method;

    private String beanName;

    private int overload = -1;

    /**
     *
     * @param testClass
     * @param method
     * @param beanName
     */
    public MockMethod(Class testClass, Method method, String beanName) {
        this.testClass = testClass;
        this.method = method;
        this.beanName = beanName;
    }

    /**
     *
     * @param testClass
     * @param method
     * @param beanName
     * @param overload
     */
    public MockMethod(Class testClass, Method method, String beanName, int overload) {
        this.testClass = testClass;
        this.method = method;
        this.beanName = beanName;
        this.overload = overload;
    }

    public Class getTestClass() {
        return testClass;
    }

    public Method getMethod() {
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
        MockMethod that = (MockMethod) o;
        return overload == that.overload && Objects.equal(testClass, that.testClass) && Objects.equal(method, that.method) && Objects.equal(beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(testClass, method, beanName, overload);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MockMethod.class.getSimpleName() + "[", "]")
                .add("testClass=" + testClass)
                .add("method=" + method)
                .add("beanName='" + beanName + "'")
                .add("overload=" + overload)
                .toString();
    }
}
