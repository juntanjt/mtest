package com.meituan.mtest.test

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.meituan.mtest.TestCase
import com.meituan.mtest.demo.user.dao.dto.UserDTO
import com.meituan.mtest.demo.user.service.vo.UserVO
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

class Yaml_Spec extends Specification {

    def "yaml2java"() {
        given:

        when:
        Yaml yaml = new Yaml()

        InputStream io = new ClassPathResource("mtestdata/UserService-getUserById/testcases.yaml").getInputStream()
        List testcases_strs = yaml.load(io)

        and:
        def testcases = Lists.newArrayList()
        for (def testcases_str in testcases_strs) {
            def testcase = new TestCase(code: testcases_str['code'], name: testcases_str['name'])

            testcases.add(testcase)
        }

        then:
        testcases_strs != null
        testcases != null
    }

    def "java2yaml"() {
        given:
        def userDTOs = [
                new UserDTO(id: 1, name: "张三", province: "北京", telephone: "13811112222"),
                new UserDTO(id: 2, name: "李四", province: "上海", telephone: "13866667777"),
                new UserDTO(id: 3, name: "王五", province: "南京", telephone: "13833334444")]
        def userVOs = [
                new UserVO(id: 1, name: "张三", province: "北京", telephone: "13811112222"),
                new UserVO(id: 2, name: "李四", province: "上海", telephone: "13866667777"),
                new UserVO(id: 3, name: "王五", province: "南京", telephone: "13833334444")]

        def userVO_map = Maps.newHashMap()
        userVO_map.put('code1', userDTOs)
        userVO_map.put('code2', userDTOs)

        Yaml yaml = new Yaml();

        when:
        def userDTOs_yaml = yaml.dump(userDTOs)
        def userVOs_yaml = yaml.dump(userDTOs)
        def userVO_map_yaml = yaml.dump(userVO_map)

        then:
        userDTOs_yaml != null
        userVOs_yaml != null
        userVO_map_yaml != null
        println(userDTOs_yaml)
        println(userVOs_yaml)
        println(userVO_map_yaml)
    }

}
