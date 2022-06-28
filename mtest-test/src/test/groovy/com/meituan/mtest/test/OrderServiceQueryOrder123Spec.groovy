package com.meituan.mtest.test

import com.meituan.mtest.MTest
import com.meituan.mtest.MTestBaseCase
import com.meituan.mtest.MockMethod
import com.meituan.mtest.demo.item.service.ItemService
import com.meituan.mtest.demo.order.service.OrderService
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
@ContextConfiguration(classes = [OrderServiceQueryOrder123Spec.class])
@ComponentScan("com.meituan.mtest.demo.order")
@ImportResource("classpath:spring-test-context.xml")
@Configuration
@MTest(testClass = OrderService.class, method = "queryOrder", location = "mtest-data123")
class OrderServiceQueryOrder123Spec extends MTestBaseCase {

    @Resource
    OrderService orderService

    @Unroll
    def "#testCase"() {
        given: ""

        when: ""
        def response = orderService.queryOrder(userId, orderId)

        then: ""
        with(response) {
            success == expected.success
        }

        Assertions.assertThat(response).usingRecursiveComparison()
                .ignoringFields("value.createTime").isEqualTo(expected)

        where: ""
        testCase << testCase()
        [userId, orderId] << request()
        expected << expected()

    }

    @Override
    MockMethod[] getMockMethods() {
        return [
                new MockMethod(ItemService.class, ItemService.class.getMethod('queryItemById', Long.class))
        ]
    }

}
