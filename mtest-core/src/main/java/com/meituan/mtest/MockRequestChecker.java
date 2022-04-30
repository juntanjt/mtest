package com.meituan.mtest;

/**
 * @author Jun Tan
 */
public interface MockRequestChecker {

    /**
     *
     * @param mocker
     * @param expectedArguments
     * @param actualArguments
     */
    void assertEquals(Mocker mocker, Object[] expectedArguments, Object[] actualArguments);

}
