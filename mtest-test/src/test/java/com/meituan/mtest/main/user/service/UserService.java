package com.meituan.mtest.main.user.service;

import com.meituan.mtest.main.user.service.vo.UserVO;

public interface UserService {

    UserVO getUserById(int uid);

    void calculateMoney(UserVO userVO);

}
