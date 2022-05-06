package com.meituan.mtest.test

import com.google.common.collect.Lists
import org.apache.commons.lang3.time.DateUtils
import org.mockito.Mockito
import org.mockito.stubbing.Answer
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method
import java.text.ParseException

/**
 *
 * @author Jun Tan
 */
class MockedStaticSpec extends Specification {

    @Unroll
    def "mock static"() {
        given: ""
        Class<?> testClass = DateUtils.class
        Method method = testClass.getMethod("parseDate", String.class, String[].class)

        when: ""
        def mockedStatic = Mockito.mockStatic(testClass)

        List<Object> args = Lists.newArrayList();
        for (Class<?> parameterType : method.getParameterTypes()) {
            args.add(Mockito.any(parameterType));
        }

        def ongoingStubbing = mockedStatic.when(() -> method.invoke(null, args.toArray()))

        Answer<?> answer = invocationOnMock -> {
            if (invocationOnMock.getArgument(0) == "今天") {
                return new Date()
            } else {
                return null
            }
        }

        ongoingStubbing = ongoingStubbing.thenAnswer(answer);

        Date today = DateUtils.parseDate("今天")
        Date yesterday = DateUtils.parseDate("昨天")

        then: ""
        today != null
        yesterday == null

        cleanup: ""
        mockedStatic.close()

        where: ""

    }

    @Unroll
    def "mock static reset"() {
        given: ""
        Class<?> testClass = DateUtils.class
        Method method = testClass.getMethod("parseDate", String.class, String[].class)

        when: ""
        def mockedStatic = Mockito.mockStatic(testClass)

        List<Object> args = Lists.newArrayList();
        for (Class<?> parameterType : method.getParameterTypes()) {
            args.add(Mockito.any(parameterType));
        }

        def ongoingStubbing = mockedStatic.when(() -> method.invoke(null, args.toArray()))

        Answer<?> answer = invocationOnMock -> {
            if (invocationOnMock.getArgument(0) == "今天") {
                return new Date()
            } else {
                return null
            }
        }

        ongoingStubbing = ongoingStubbing.thenAnswer(answer);

        mockedStatic.close()

        Date today = DateUtils.parseDate("今天")

        then: ""
        thrown(ParseException.class)

        where: ""

    }

    @Unroll
    def "mock static no arg"() {
        given: ""
        Class<?> testClass = Lists.class
        Method method = testClass.getMethod("newArrayList")

        when: ""
        def mockedStatic = Mockito.mockStatic(testClass)

        List<Object> args = Lists.newArrayList();
        for (Class<?> parameterType : method.getParameterTypes()) {
            args.add(Mockito.any(parameterType));
        }

        def ongoingStubbing = mockedStatic.when(() -> method.invoke(null, args.toArray()))

        Answer<?> answer = invocationOnMock -> {
            List<?> mockedList = new ArrayList<>()
            mockedList.add(1)
            return mockedList
        }

        ongoingStubbing = ongoingStubbing.thenAnswer(answer);

        List<?> list = Lists.newArrayList()

        then: ""
        list != null
        list.size() == 1
        list.get(0) == 1

        cleanup: ""
        mockedStatic.close()

        where: ""

    }

}