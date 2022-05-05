package com.meituan.mtest.demo.user.service.impl;

import com.meituan.mtest.MTestException;
import com.meituan.mtest.demo.user.dao.MoneyDAO;
import com.meituan.mtest.demo.user.dao.UserDAO;
import com.meituan.mtest.demo.user.service.UserService;
import com.meituan.mtest.demo.user.dao.dto.UserDTO;
import com.meituan.mtest.demo.user.service.vo.OrderVO;
import com.meituan.mtest.demo.user.service.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private MoneyDAO moneyDAO;
    @Resource
    private UserDAO userDAO;

    public UserVO getUserById(int uid) {
        if (uid == -1) {
            throw new MTestException("uid="+uid);
        }

        List<UserDTO> users = userDAO.getUserInfo();
        UserDTO userDTO = users.stream().filter(u -> u.getId() == uid).findFirst().orElse(null);
        UserVO userVO = new UserVO();
        if (null == userDTO) {
            return userVO;
        }
        userVO.setId(userDTO.getId());
        userVO.setName(userDTO.getName());
        userVO.setSex(userDTO.getSex());
        userVO.setAge(userDTO.getAge());
        // 显示邮编
        if ("上海".equals(userDTO.getProvince())) {
            userVO.setAbbreviation("沪");
            userVO.setPostCode("200000");
        }
        if ("北京".equals(userDTO.getProvince())) {
            userVO.setAbbreviation("京");
            userVO.setPostCode("100000");
        }
        // 手机号处理
        if (null != userDTO.getTelephone() && !"".equals(userDTO.getTelephone())) {
            userVO.setTelephone(userDTO.getTelephone().substring(0, 3) + "****" + userDTO.getTelephone().substring(7));
        }

        return userVO;
    }

    /**
     * 根据汇率计算金额
     * @param userVO
     */
    public void calculateMoney(UserVO userVO){
        if(null == userVO.getUserOrders() || userVO.getUserOrders().size() <= 0){
            return ;
        }
        for(OrderVO orderVO : userVO.getUserOrders()){
            BigDecimal amount = orderVO.getAmount();
            BigDecimal exchange = moneyDAO.getRate(userVO.getCountry());
            amount = amount.multiply(exchange);
            orderVO.setAmount(amount);
        }
    }
}
