package com.meituan.mtest.demo.user.dao;

import com.meituan.mtest.demo.user.dao.dto.UserDTO;

import java.util.List;

public interface UserDAO {

    List<UserDTO> getUserInfo();
}
