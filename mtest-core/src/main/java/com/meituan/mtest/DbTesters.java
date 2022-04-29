package com.meituan.mtest;

import org.dbunit.Assertion;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.*;

public class DbTesters {

    public static IDatabaseTester newDatabaseTester(DataSource dataSource) throws ClassNotFoundException {
        if (dataSource != null) {
            return new DataSourceDatabaseTester(dataSource);
        } else {
            return new JdbcDatabaseTester("org.h2.Driver",
                    "jdbc:h2:mem:PCTDiscount;MODE=MYSQL;DB_CLOSE_DELAY=-1");
        }
    }

    public static void setUp(String classSimpleName, String methodName, TestCase testCase, IDatabaseTester databaseTester) throws Exception {
        String testPath = "mtest-data/" + classSimpleName + "-" + methodName + "/db-data-setUp.xml";
        String casePath = "mtest-data/" + classSimpleName + "-" + methodName + "/db-data/" + testCase.getId() + "-setUp.xml";

        if (new ClassPathResource(testPath).exists()) {
            setUp(databaseTester, new ClassPathResource(testPath).getFile());
        }
        if (new ClassPathResource(casePath).exists()) {
            setUp(databaseTester, new ClassPathResource(casePath).getFile());
        }
        databaseTester.onSetup();
    }

    public static void setUp(String classSimpleName, String methodName, TestCase testCase, int overload, IDatabaseTester databaseTester) throws Exception {
        String testPath = "mtest-data/" + classSimpleName + "-" + methodName + "-" + overload + "/db-data-setUp.xml";
        String casePath = "mtest-data/" + classSimpleName + "-" + methodName + "-" + overload + "/db-data/" + testCase.getId() + "-setUp.xml";

        if (new ClassPathResource(testPath).exists()) {
            setUp(databaseTester, new ClassPathResource(testPath).getFile());
        }
        if (new ClassPathResource(casePath).exists()) {
            setUp(databaseTester, new ClassPathResource(casePath).getFile());
        }
        databaseTester.onSetup();
    }

    public static void setUp(IDatabaseTester databaseTester, File file) throws FileNotFoundException, DataSetException {
        IDataSet setUpDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(file));
        databaseTester.setDataSet(setUpDataSet);
    }

    public static void tearDown(IDatabaseTester databaseTester) throws Exception {
        databaseTester.onTearDown();
    }

    public static void verifyData(String classSimpleName, String methodName, TestCase testCase, IDatabaseTester databaseTester) throws Exception {
        String casePath = "mtest-data/" + classSimpleName + "-" + methodName + "/db-data/" + testCase.getId() + "-expected.xml";

        if (new ClassPathResource(casePath).exists()) {
            verifyData(databaseTester, new ClassPathResource(casePath).getFile());
        }
    }

    public static void verifyData(String classSimpleName, String methodName, TestCase testCase, int overload, IDatabaseTester databaseTester) throws Exception {
        String casePath = "mtest-data/" + classSimpleName + "-" + methodName + "-" + overload + "/db-data/" + testCase.getId() + "-expected.xml";

        if (new ClassPathResource(casePath).exists()) {
            verifyData(databaseTester, new ClassPathResource(casePath).getFile());
        }
    }

    public static void verifyData(IDatabaseTester databaseTester, File file) throws Exception {
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(file));
        String[] tableNames = expectedDataSet.getTableNames();

        if (tableNames==null || tableNames.length==0) {
            return;
        }
        for (String tableName : tableNames) {
            ITable expectedTable = expectedDataSet.getTable(tableName);

            IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
            ITable actualTable = databaseDataSet.getTable(tableName);

            ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable,
                    expectedTable.getTableMetaData().getColumns());
            Assertion.assertEquals(expectedTable, filteredTable);
        }
    }

}
