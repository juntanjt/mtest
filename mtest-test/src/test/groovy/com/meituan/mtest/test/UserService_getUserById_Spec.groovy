package com.meituan.mtest.test

import com.meituan.mtest.MtestBaseCase
import com.meituan.mtest.TestMethod
import com.meituan.mtest.demo.user.dao.UserDAO
import com.meituan.mtest.demo.user.service.UserService
import org.assertj.core.api.Assertions
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import javax.annotation.Resource

@ContextConfiguration(classes = [UserService_getUserById_Spec.class])
@ComponentScan("com.meituan.mtest.demo")
@Configuration
class UserService_getUserById_Spec extends MtestBaseCase {

    @Resource
    UserService userService

    @Unroll
    def "#testcase.name 手机号 #expected.telephone"() {
        given: "设置请求参数"
        mock(testcase)

        when: "获取用户信息"
        def response = userService.getUserById(uid)

        then: "验证返回结果"
        with(response) {
            postCode == expected.postCode
            telephone == expected.telephone
        }

        Assertions.assertThat(response).usingRecursiveComparison().isEqualTo(expected);

        where: "经典之处：表格方式验证用户信息的分支场景"
        testcase << this.testCase()
        [uid] << this.request()
        expected << this.response()

    }

    @Override
    TestMethod getTestMethod() {
        return new TestMethod(UserService.class.getMethod('getUserById', int), UserService.class, 'userService')
    }

    @Override
    TestMethod[] getMockMethods() {
        return [
                new TestMethod(UserDAO.class.getMethod('getUserInfo'), UserDAO.class, 'userDAO')
        ]
    }
}