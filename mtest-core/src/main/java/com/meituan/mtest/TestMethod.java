package com.meituan.mtest;

import java.lang.reflect.Method;

public class TestMethod {

    private Method method;

    private Class testClass;

    private String beanName;

    private int overload = -1;

    public TestMethod(Method method, Class testClass, String beanName) {
        this.method = method;
        this.testClass = testClass;
        this.beanName = beanName;
    }

    public TestMethod(Method method, Class testClass, String beanName, int overload) {
        this.method = method;
        this.testClass = testClass;
        this.beanName = beanName;
        this.overload = overload;
    }

    public Method getMethod() {
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
}
