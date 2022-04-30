package com.meituan.mtest;

import org.dbunit.dataset.ITable;

/**
 * @author Jun Tan
 */
public interface DBChecker {

    /**
     *
     * @param expectedTable
     * @param actualTable
     */
    void assertEquals(String tableName, ITable expectedTable, ITable actualTable);

}
