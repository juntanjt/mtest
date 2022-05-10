package com.meituan.mtest;

import com.google.common.base.Objects;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 *
 * @author Jun Tan
 */
public class MockMethod {

    private Class<?> mockClass;

    private Method method;

    private String beanName;

    private int overload = -1;

    /**
     *
     * @param mockClass
     * @param method
     */
    public MockMethod(Class<?> mockClass, Method method) {
        this.mockClass = mockClass;
        this.method = method;
    }

    /**
     *
     * @param mockClass
     * @param method
     * @param beanName
     */
    public MockMethod(Class<?> mockClass, Method method, String beanName) {
        this.mockClass = mockClass;
        this.method = method;
        this.beanName = beanName;
    }

    /**
     *
     * @param mockClass
     * @param method
     * @param overload
     */
    public MockMethod(Class<?> mockClass, Method method, int overload) {
        this.mockClass = mockClass;
        this.method = method;
        this.overload = overload;
    }

    /**
     *
     * @param mockClass
     * @param method
     * @param beanName
     * @param overload
     */
    public MockMethod(Class<?> mockClass, Method method, String beanName, int overload) {
        this.mockClass = mockClass;
        this.method = method;
        this.beanName = beanName;
        this.overload = overload;
    }

    public Class<?> getMockClass() {
        return mockClass;
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
        return overload == that.overload && Objects.equal(mockClass, that.mockClass) && Objects.equal(method, that.method) && Objects.equal(beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mockClass, method, beanName, overload);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MockMethod.class.getSimpleName() + "[", "]")
                .add("mockClass=" + mockClass)
                .add("method=" + method)
                .add("beanName='" + beanName + "'")
                .add("overload=" + overload)
                .toString();
    }
}
