package com.meituan.mtest;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.assertj.core.util.Lists;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jun Tan
 */
public class DBTester {

    private IDatabaseTester databaseTester;
    private DBChecker dbChecker = new DefaultDBChecker();
    private final Map<String, DBChecker> dbCheckers = Maps.newHashMap();

    /**
     *
     */
    private DBTester() {
    }

    /**
     *
     * @return
     */
    public static DBTester newInstance() {
        return new DBTester();
    }

    /**
     *
     */
    public void initDatabaseTester() {
        initDatabaseTester(null);
    }

    /**
     *
     * @param dataSource
     */
    public void initDatabaseTester(DataSource dataSource) {
        try {
            if (dataSource != null) {
                databaseTester = new DataSourceDatabaseTester(dataSource);
            } else {
                databaseTester = new JdbcDatabaseTester("org.h2.Driver",
                        "jdbc:h2:mem:PCTDiscount;MODE=MYSQL;DB_CLOSE_DELAY=-1");
            }
        } catch (Exception e) {
            throw new MTestException("init database tester error", e);
        }
    }

    /**
     *
     * @param dbChecker
     */
    public void registerDBChecker(DBChecker dbChecker) {
        if (dbChecker != null) {
            this.dbChecker = dbChecker;
        }
    }

    /**
     *
     * @param dbCheckers
     */
    public void registerDBCheckers(Map<String, DBChecker> dbCheckers) {
        if (dbCheckers != null) {
            dbCheckers.putAll(dbCheckers);
        }
    }

    /**
     *
     */
    public void cleanup() {
        try {
            dbCheckers.clear();
            databaseTester.onTearDown();
        } catch (Exception e) {
            throw new MTestException("db tester cleanup error", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCase
     */
    public void setUp(TestMethod testMethod, TestCase testCase) {
        String testPath = PathConvention.getTestMethodDBSetUpPath(testMethod);
        String casePath = PathConvention.getTestCaseDBSetUpPath(testMethod, testCase);

        setUp(Lists.newArrayList(testPath, casePath));
    }

    /**
     *
     * @param paths
     */
    public void setUp(List<String> paths) {
        try {
            for (String path : paths) {
                IDataSet dataSet = loadDataSet(path);
                if (dataSet != null) {
                    databaseTester.setDataSet(loadDataSet(path));
                }
            }
            databaseTester.onSetup();
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("db tester setUp error, paths [" + paths.toArray() + "]", e);
        }
    }

    /**
     *
     * @param testMethod
     * @param testCase
     */
    public void verifyData(TestMethod testMethod, TestCase testCase) {
        String testPath = PathConvention.getTestMethodDBExpectedPath(testMethod);
        String casePath = PathConvention.getTestCaseDBExpectedPath(testMethod, testCase);

        verifyData(Lists.newArrayList(testPath, casePath));
    }

    /**
     *
     * @param paths
     */
    public void verifyData(List<String> paths) {
        for (String path : paths) {
            verifyData(path);
        }
    }

    /**
     *
     * @param path
     */
    public void verifyData(String path) {
        try {
            IDataSet expectedDataSet = loadDataSet(path);
            if (expectedDataSet == null) {
                return;
            }
            String[] tableNames = expectedDataSet.getTableNames();

            if (tableNames==null || tableNames.length==0) {
                return;
            }
            for (String tableName : tableNames) {
                DBChecker dbChecker = dbCheckers.get(tableName);
                if (dbChecker == null) {
                    dbChecker = this.dbChecker;
                }

                ITable expectedTable = expectedDataSet.getTable(tableName);

                IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
                ITable actualTable = databaseDataSet.getTable(tableName);

                dbChecker.assertEquals(tableName, expectedTable, actualTable);
            }
        } catch (Exception e) {
            Throwables.propagateIfInstanceOf(e, MTestException.class);
            throw new MTestException("db tester verify error, path [" + path + "]", e);
        }
    }

    /**
     *
     * @param path
     * @return
     */
    private IDataSet loadDataSet(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            if (! resource.exists()) {
                return null;
            }
            return new FlatXmlDataSetBuilder().build(new ClassPathResource(path).getInputStream());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
