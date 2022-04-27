package com.meituan.mtest.test

import com.meituan.mtest.main.user.dao.UserDAO
import com.meituan.mtest.main.user.service.UserService
import com.meituan.mtest.main.user.dao.dto.UserDTO
import org.mockito.Mockito
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import javax.annotation.Resource

@ContextConfiguration(classes = [UserService_getUserById_Spec.class])
@ComponentScan("com.meituan.mtest.main")
@Configuration
class UserService_getUserById_Spec extends AbcMtestGroovyCase {

    @Resource
    UserService userService
    @Resource
    UserDAO userDAO

    @Unroll
    def "手机号 #user.telephone"() {
        given: "设置请求参数"
        def users = [
                new UserDTO(id: 1, name: "张三", province: "北京", telephone: "13811112222"),
                new UserDTO(id: 2, name: "李四", province: "上海", telephone: "13866667777"),
                new UserDTO(id: 3, name: "王五", province: "南京", telephone: "13833334444")]

        Mockito.when(userDAO.getUserInfo()).thenReturn(users)

        when: "获取用户信息"
        def response = userService.getUserById(uid)

        then: "验证返回结果"
        with(response) {
            uid == id
            postCode == postCodeResult
            telephone == telephoneResult
        }

//        Assertions.assertThat(response).isEqualToComparingFieldByFieldRecursively(user)

        where: "经典之处：表格方式验证用户信息的分支场景"
        uid | user1                        || postCodeResult | telephoneResult
        1   | null || "100000"       | "138****2222"
        2   | null || "200000"       | "138****7777"
        3   | null || null           | "138****4444"

        and:
        user << [
                new UserDTO(id: 1, name: "张三", province: "北京", telephone: "13811112222"),
                new UserDTO(id: 2, name: "李四", province: "上海", telephone: "13866667777"),
                new UserDTO(id: 3, name: "王五", province: "南京", telephone: "13833334444")]

    }


}