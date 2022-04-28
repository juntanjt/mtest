package com.meituan.mtest.demo.user.dao.impl;

import com.meituan.mtest.demo.user.dao.MoneyDAO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository("moneyDAO")
public class MoneyDAOImpl implements MoneyDAO {

    public BigDecimal getRate(String Ccountry) {
        return null;
    }
}
