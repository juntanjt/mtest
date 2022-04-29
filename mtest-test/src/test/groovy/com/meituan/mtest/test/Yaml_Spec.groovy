package com.meituan.mtest.test

import com.google.common.collect.Maps
import com.meituan.mtest.demo.user.dao.dto.UserDTO
import com.meituan.mtest.demo.user.service.vo.UserVO
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

class Yaml_Spec extends Specification {

    def "yaml2java"() {
        given:
        InputStream io = new ClassPathResource("mtest-data/UserService-getUserById/request.yaml").getInputStream()

        when:
        Yaml yaml = new Yaml()
        Map request_strs = yaml.load(io)

        then:
        request_strs != null
    }

    def "java2yaml"() {
        given:
        def userDTOs = [
                new UserDTO(id: 1, name: "张三", province: "北京", telephone: "13811112222"),
                new UserDTO(id: 2, name: "李四", province: "上海", telephone: "13866667777"),
                new UserDTO(id: 3, name: "王五", province: "南京", telephone: "13833334444")]
        def userVOs = [
                new UserVO(id: 1, name: "张三", province: "京", telephone: "138****2222"),
                new UserVO(id: 2, name: "李四", province: "沪", telephone: "138****7777"),
                new UserVO(id: 3, name: "王五", province: null, telephone: "138****4444")]

        def userVO_map = Maps.newHashMap()
        userVO_map.put('code1', userVOs[0])
        userVO_map.put('code2', userVOs[1])

        Yaml yaml = new Yaml();

        when:
        def userDTOs_yaml = yaml.dump(userDTOs)
        def userVOs_yaml = yaml.dump(userVOs)
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
