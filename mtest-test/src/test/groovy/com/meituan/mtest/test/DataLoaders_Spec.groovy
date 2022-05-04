package com.meituan.mtest.test

import com.meituan.mtest.DataLoaders
import com.meituan.mtest.MTest
import com.meituan.mtest.TestMethod
import com.meituan.mtest.demo.user.service.UserService
import spock.lang.Specification

/**
 *
 * @author Jun Tan
 */
class DataLoaders_Spec extends Specification {

    def "loadTestCases"() {
        given:

        when:
        def testcases = DataLoaders.loadTestCases(new TestMethod(UserService.class, "getUserById"))

        then:
        testcases != null
    }

    def "loadRequests"() {
        given:
        def testcases = DataLoaders.loadTestCases(new TestMethod(UserService.class, "getUserById"))

        when:
        def requests = DataLoaders.loadRequests(new TestMethod(UserService.class, "getUserById"))

        then:
        requests != null
    }

}
