package com.meituan.mtest.test

import com.meituan.mtest.MTest
import com.meituan.mtest.MTestBaseCase
import com.meituan.mtest.MockMethod
import com.meituan.mtest.demo.user.dao.UserDAO
import com.meituan.mtest.demo.user.service.UserService
import org.assertj.core.api.Assertions
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import javax.annotation.Resource

/**
 *
 * @author Jun Tan
 */
@ContextConfiguration(classes = [UserService_getUserById_Spec.class])
@ComponentScan("com.meituan.mtest.demo.user")
@ImportResource("classpath:spring-ddl.xml")
@Configuration
@MTest(testClass = UserService.class, method = "getUserById")
class UserService_getUserById_Spec extends MTestBaseCase {

    @Resource
    UserService userService

    @Unroll
    def "#testcase 手机号 #expected.telephone"() {
        given: "设置请求参数"

        when: "获取用户信息"
        def response = userService.getUserById(uid)

        then: "验证返回结果"
        with(response) {
            postCode == expected.postCode
            telephone == expected.telephone
        }

        Assertions.assertThat(response).usingRecursiveComparison().isEqualTo(expected);

        where: "经典之处：表格方式验证用户信息的分支场景"
        testcase << testCase()
        [uid] << request()
        expected << expected()

    }

    @Override
    MockMethod[] getMockMethods() {
        return [
                new MockMethod(UserDAO.class.getMethod('getUserInfo'), UserDAO.class, 'userDAO')
        ]
    }
}