package com.meituan.mtest.main.user.dao.impl;

import com.meituan.mtest.main.user.dao.UserDAO;
import com.meituan.mtest.main.user.dao.dto.UserDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {
    public List<UserDTO> getUserInfo() {
        return null;
    }
}
