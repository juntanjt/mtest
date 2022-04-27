package com.meituan.mtest.test

import com.meituan.mtest.DataLoaders
import spock.lang.Specification

class DataLoaders_Spec extends Specification {

    def "loadTestCases"() {
        given:

        when:
        def testcases = DataLoaders.loadTestCases("UserService", "getUserById")

        then:
        testcases != null
    }

    def "loadRequests"() {
        given:
        def testcases = DataLoaders.loadTestCases("UserService", "getUserById")

        when:
        def requests = DataLoaders.loadRequests("UserService", "getUserById", testcases)

        then:
        requests != null
    }

}
