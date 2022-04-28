package com.meituan.mtest.demo.user.service;

import com.meituan.mtest.demo.user.service.vo.UserVO;

public interface UserService {

    UserVO getUserById(int uid);

    void calculateMoney(UserVO userVO);

}
