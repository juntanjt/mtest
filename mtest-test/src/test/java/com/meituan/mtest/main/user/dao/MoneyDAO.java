package com.meituan.mtest.main.user.dao;

import java.math.BigDecimal;

public interface MoneyDAO {

    BigDecimal getRate(String Ccountry);

}
