package com.meituan.mtest.test

import com.meituan.mtest.demo.item.service.dto.ItemDTO
import com.meituan.mtest.demo.order.dao.OrderDO
import com.meituan.mtest.demo.order.service.dto.OrderDTO
import com.meituan.mtest.demo.order.service.dto.OrderReqDTO
import com.meituan.mtest.demo.order.service.dto.ResultDTO
import org.assertj.core.util.Maps
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

/**
 *
 * @author Jun Tan
 */
class YamlSpec extends Specification {

    def "java2yaml"() {
        given:
        def yaml = new Yaml()

        and:
        def itemDTO = new ItemDTO(itemId:33333, itemName:"农夫山泉", price: 1.5)

        def orderDO = new OrderDO(id:111, userId:2222, itemId:33333, itemCount:2, amount:3.0, orderName:"农夫山泉", address:"上海", telephone:"13000000000", createTime:new Date())

        def orderDTO = new OrderDTO(orderId:111, userId:2222, itemId:33333, itemCount:2, amount:3.0, orderName:"农夫山泉", address:"上海", telephone:"13000000000", createTime:new Date())

        def orderReqDTO = new OrderReqDTO(itemId:33333, itemCount:2, address:"上海", telephone:"13000000000")

        def orderResultDTO = ResultDTO.of(orderDTO)

        when:
        def OrderService_createOder_request_yaml = yaml.dump(Maps.newHashMap("case_id_111", [2222, orderReqDTO]))
        def OrderService_createOder_expected_yaml = yaml.dump(Maps.newHashMap("case_id_111", ResultDTO.of(111)))
        def mock_ItemService_queryItemById_request_yaml = yaml.dump(Maps.newHashMap("case_id_111", [33333]))
        def mock_ItemService_queryItemById_response_yaml = yaml.dump(Maps.newHashMap("case_id_111", itemDTO))

        def OrderService_queryOrder_request_yaml = yaml.dump(Maps.newHashMap("case_id_333", [111]))
        def OrderService_queryOrder_expected_yaml = yaml.dump(Maps.newHashMap("case_id_333", orderResultDTO))

        then:
        OrderService_createOder_request_yaml != null
        println("OrderService_createOder_request_yaml\n" + OrderService_createOder_request_yaml)
        OrderService_createOder_expected_yaml != null
        println("OrderService_createOder_expected_yaml\n" + OrderService_createOder_expected_yaml)
        mock_ItemService_queryItemById_request_yaml != null
        println("mock_ItemService_queryItemById_request_yaml\n" + mock_ItemService_queryItemById_request_yaml)
        mock_ItemService_queryItemById_response_yaml != null
        println("mock_ItemService_queryItemById_response_yaml\n" + mock_ItemService_queryItemById_response_yaml)
        OrderService_queryOrder_request_yaml != null
        println("OrderService_queryOrder_request_yaml\n" + OrderService_queryOrder_request_yaml)
        OrderService_queryOrder_expected_yaml != null
        println("OrderService_queryOrder_expected_yaml\n" + OrderService_queryOrder_expected_yaml)
    }

}
