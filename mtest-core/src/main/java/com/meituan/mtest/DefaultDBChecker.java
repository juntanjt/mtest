package com.meituan.mtest;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.dbunit.Assertion;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;

import java.util.List;
import java.util.Map;

/**
 * @author Jun Tan
 */
public class DefaultDBChecker implements DBChecker {

    private static final Map<Class<?>, Object> PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES = Maps.newHashMap();

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
        if (value == null) {
            return true;
        }
        if (PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.containsKey(value.getClass())) {
            return value.equals(PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.get(value.getClass()));
        }
        return false;
    }

    public DefaultDBChecker setDbCheckerMap(Map<String, DBChecker> dbCheckerMap) {
        this.dbCheckerMap = dbCheckerMap;
        return this;
    }

    static {
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Boolean.class, false);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Character.class, '\u0000');
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Byte.class, (byte) 0);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Short.class, (short) 0);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Integer.class, 0);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Long.class, 0L);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Float.class, 0F);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(Double.class, 0D);

        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(boolean.class, false);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(char.class, '\u0000');
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(byte.class, (byte) 0);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(short.class, (short) 0);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(int.class, 0);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(long.class, 0L);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(float.class, 0F);
        PRIMITIVE_OR_WRAPPER_DEFAULT_VALUES.put(double.class, 0D);
    }
}



