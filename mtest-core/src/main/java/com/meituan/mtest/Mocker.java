package com.meituan.mtest;

import com.google.common.base.Objects;

/**
 *
 * @author Jun Tan
 */
public class Mocker {

    private String classSimpleName;

    private String methodName;

    private int overload = -1;

    public String getClassSimpleName() {
        return classSimpleName;
    }

    public void setClassSimpleName(String classSimpleName) {
        this.classSimpleName = classSimpleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getOverload() {
        return overload;
    }

    public void setOverload(int overload) {
        this.overload = overload;
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
        return "classSimpleName='" + classSimpleName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", overload=" + overload;
    }
}
