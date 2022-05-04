package com.meituan.mtest;

import com.google.common.base.Objects;

import java.util.StringJoiner;

/**
 *
 * @author Jun Tan
 */
public class Mocker {

    private String classSimpleName;

    private String methodName;

    private int overload = -1;

    /**
     *
     * @param classSimpleName
     * @param methodName
     */
    public Mocker(String classSimpleName, String methodName) {
        this.classSimpleName = classSimpleName;
        this.methodName = methodName;
    }

    public Mocker(String classSimpleName, String methodName, int overload) {
        this.classSimpleName = classSimpleName;
        this.methodName = methodName;
        this.overload = overload;
    }

    public String getClassSimpleName() {
        return classSimpleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getOverload() {
        return overload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mocker mocker = (Mocker) o;
        return overload == mocker.overload && Objects.equal(classSimpleName, mocker.classSimpleName) && Objects.equal(methodName, mocker.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(classSimpleName, methodName, overload);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Mocker.class.getSimpleName() + "[", "]")
                .add("classSimpleName='" + classSimpleName + "'")
                .add("methodName='" + methodName + "'")
                .add("overload=" + overload)
                .toString();
    }
}
