package com.meituan.mtest;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.dbunit.Assertion;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;

import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jun Tan
 */
public class DefaultDBChecker implements DBChecker {

    private static final char IGNORE_CHAR = '_';

    private Map<String, DBChecker> dbCheckerMap;

    /**
     *
     * @param expectedTable
     * @param actualTable
     */
    @Override
    public void assertEquals(String tableName, ITable expectedTable, ITable actualTable) {
        if (dbCheckerMap != null && dbCheckerMap.get(tableName) != null) {
            dbCheckerMap.get(tableName).assertEquals(tableName, expectedTable, actualTable);
        } else {
            assertEquals(expectedTable, actualTable);
        }
    }

    /**
     *
     * @param expectedTable
     * @param actualTable
     */
    private void assertEquals(ITable expectedTable, ITable actualTable) {
        try {
            if (expectedTable.getRowCount() == 0) {
                Assertion.assertEquals(expectedTable, actualTable);
                return;
            }

            Column[] columns = expectedTable.getTableMetaData().getColumns();
            List<Column> includeds = Lists.newArrayList();

            for (Column column : columns) {
                Object value = expectedTable.getValue(0, column.getColumnName());
                if (value == null) {
                    continue;
                }
                boolean ignore = isIgnoreFlagValue(column.getDataType().getSqlType(), value);
                if (! ignore) {
                    includeds.add(column);
                }
            }

            ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable, includeds.toArray(new Column[0]));
            Assertion.assertEquals(expectedTable, filteredTable);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

    }

    private boolean isIgnoreFlagValue(int sqlType, Object value) {
        boolean ignore = false;
        switch (sqlType) {
            // StringDataType
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                ignore = String.valueOf(IGNORE_CHAR).equals(value);
                break;
            // IntegerDataType, BigIntegerDataType, LongDataType
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                ignore = Integer.valueOf(Integer.MIN_VALUE).equals(value);
                break;
            // FloatDataType, DoubleDataType
            case Types.FLOAT:
            case Types.DOUBLE:
                ignore = Double.valueOf(Integer.MIN_VALUE).equals(value);
                break;
            // DateDataType, TimeDataType, TimestampDataType
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                ignore = new Date().equals(value);
                break;
            default:
                break;
        }
        return ignore;
    }

    public DefaultDBChecker setDbCheckerMap(Map<String, DBChecker> dbCheckerMap) {
        this.dbCheckerMap = dbCheckerMap;
        return this;
    }
}



