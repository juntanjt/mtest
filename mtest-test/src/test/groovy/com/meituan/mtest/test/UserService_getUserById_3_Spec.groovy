package com.meituan.mtest.test

import com.meituan.mtest.SpringBeanRegistryUtil
import com.meituan.mtest.demo.user.dao.UserDAO
import com.meituan.mtest.demo.user.dao.dto.UserDTO
import com.meituan.mtest.demo.user.service.UserService
import org.dbunit.Assertion
import org.dbunit.JdbcDatabaseTester
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ITable
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource

@ContextConfiguration(classes = [UserService_getUserById_3_Spec.class])
//@ComponentScan("com.meituan.mtest.demo.user")
@ImportResource("classpath:spring-context.xml")
@Configuration
class UserService_getUserById_3_Spec extends Specification implements BeanFactoryPostProcessor {

    @Resource
    UserService userService

    @Unroll
    def "手机号 #user.telephone"() {
        given: "设置请求参数"
        def databaseTester = new JdbcDatabaseTester("org.h2.Driver",
                "jdbc:h2:mem:PCTDiscount;MODE=MYSQL;DB_CLOSE_DELAY=-1")

        IDataSet setUpDataSet = new FlatXmlDataSetBuilder().build(new ClassPathResource("mtest-data/UserService-getUserById/db-data/id1-setUp.xml").getInputStream());
        databaseTester.setDataSet(setUpDataSet)
        databaseTester.onSetup()

        // Fetch database data after executing your code
        IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable("USER_TABLE");

        // Load expected data from an XML dataset
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new ClassPathResource("mtest-data/UserService-getUserById/db-data/id1-expected.xml").getInputStream());
        ITable expectedTable = expectedDataSet.getTable("USER_TABLE");

        // Assert actual database table match expected table
        ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable,
                expectedTable.getTableMetaData().getColumns());
        Assertion.assertEquals(expectedTable, filteredTable);

        when: "获取用户信息"
        def response = userService.getUserById(uid)

        then: "验证返回结果"
        with(response) {
            uid == id
            postCode == postCodeResult
            telephone == telephoneResult
        }

        and:
        databaseTester.onTearDown()

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