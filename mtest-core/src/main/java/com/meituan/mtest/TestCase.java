package com.meituan.mtest;

import com.google.common.base.Objects;

import java.util.StringJoiner;

/**
 *
 * @author Jun Tan
 */
public class TestCase {

    private String id;

    private String name;

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
     * @param ignore
     */
    public TestCase(String id, String name, boolean ignore) {
        this.id = id;
        this.name = name;
        this.ignore = ignore;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isIgnore() {
        return ignore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCase testCase = (TestCase) o;
        return ignore == testCase.ignore && Objects.equal(id, testCase.id) && Objects.equal(name, testCase.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, ignore);
    }

    @Override
    public String toString() {
        return new StringJoiner("")
                .add("id='" + id + "', ")
                .add("name=" + name)
                .toString();
    }
}
