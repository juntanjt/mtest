package com.meituan.mtest;

import com.google.common.base.Objects;

import java.util.StringJoiner;

/**
 *
 * @author Jun Tan
 */
public class TestCase {

    public static final int NORMAL = 0;
    public static final int EXCEPTION = 1;

    private String id;

    private String name;

    private boolean isException = false;

    private boolean ignore = false;

    /**
     * 
     * @param id
     * @param name
     */
    public TestCase(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @param id
     * @param name
     */
    public TestCase(String id, String name, boolean isException) {
        this.id = id;
        this.name = name;
        this.isException = isException;
    }

    public TestCase(String id, String name, boolean isException, boolean ignore) {
        this.id = id;
        this.name = name;
        this.isException = isException;
        this.ignore = ignore;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isException() {
        return isException;
    }

    public boolean isIgnore() {
        return ignore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCase testCase = (TestCase) o;
        return isException == testCase.isException && ignore == testCase.ignore && Objects.equal(id, testCase.id) && Objects.equal(name, testCase.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, isException, ignore);
    }

    @Override
    public String toString() {
        return new StringJoiner("")
                .add(id + ", ")
                .add(name)
                .toString();
    }
}
