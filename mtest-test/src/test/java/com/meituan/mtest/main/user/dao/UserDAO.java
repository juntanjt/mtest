package com.meituan.mtest.main.user.dao;

import com.meituan.mtest.main.user.dao.dto.UserDTO;

import java.util.List;

public interface UserDAO {

    List<UserDTO> getUserInfo();
}
