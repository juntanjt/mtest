package com.meituan.mtest;

import java.util.Map;

/**
 * @author Jun Tan
 */
public class DefaultMockRequestChecker implements MockRequestChecker {

    private Map<Mocker, MockRequestChecker> mockRequestCheckerMap;

    @Override
    public void assertEquals(Mocker mocker, Object[] expectedArguments, Object[] actualArguments) {
        if (mockRequestCheckerMap != null && mockRequestCheckerMap.get(mocker) != null) {
            mockRequestCheckerMap.get(mocker).assertEquals(mocker, expectedArguments, actualArguments);
        } else {
            assertEquals(expectedArguments, actualArguments);
        }
    }

    /**
     *
     * @param expectedArguments
     * @param actualArguments
     */
    private void assertEquals(Object[] expectedArguments, Object[] actualArguments) {
        return;
    }

    public DefaultMockRequestChecker setMockRequestCheckerMap(Map<Mocker, MockRequestChecker> mockRequestCheckerMap) {
        this.mockRequestCheckerMap = mockRequestCheckerMap;
        return this;
    }
}
