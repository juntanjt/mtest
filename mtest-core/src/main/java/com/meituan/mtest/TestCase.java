package com.meituan.mtest;

import com.google.common.base.Objects;

/**
 *
 * @author Jun Tan
 */
public class TestCase {

    private String id;

    private String name;

    private boolean ignore = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCase testCase = (TestCase) o;
        return Objects.equal(id, testCase.id) && Objects.equal(name, testCase.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name);
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", name='" + name + '\'';
    }
}
