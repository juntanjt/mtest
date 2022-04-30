package com.meituan.mtest.test

import com.meituan.mtest.SpringBeanRegistryUtil
import com.meituan.mtest.demo.user.dao.UserDAO
import com.meituan.mtest.demo.user.service.UserService
import com.meituan.mtest.demo.user.dao.dto.UserDTO
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource

/**
 *
 * @author Jun Tan
 */
@ContextConfiguration(classes = [UserService_getUserById_2_Spec.class])
//@ComponentScan("com.meituan.mtest.demo.user")
@ImportResource("classpath:spring-context.xml")
@Configuration
class UserService_getUserById_2_Spec extends Specification implements BeanFactoryPostProcessor {

    @Resource
    UserService userService

    @Unroll
    def "手机号 #user.telephone"() {
        given: "设置请求参数"

        when: "获取用户信息"
        def response = userService.getUserById(uid)

        then: "验证返回结果"
        with(response) {
            uid == id
            postCode == postCodeResult
            telephone == telephoneResult
        }

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

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        def user = [
                new UserDTO(id: 1, name: "张三", province: "北京", telephone: "13811112222"),
                new UserDTO(id: 2, name: "李四", province: "上海", telephone: "13866667777"),
                new UserDTO(id: 3, name: "王五", province: "南京", telephone: "13833334444")]

        def userDAO = Mock(UserDAO)
        userDAO.getUserInfo() >> user
        SpringBeanRegistryUtil.registerSingleton(beanFactory, "userDAO", userDAO)
    }
}