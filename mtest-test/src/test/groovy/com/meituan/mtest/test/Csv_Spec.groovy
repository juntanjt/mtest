package com.meituan.mtest.test

import com.google.common.collect.Lists
import com.meituan.mtest.TestCase
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import spock.lang.Specification;

class Csv_Spec extends Specification {

    def "csv2java"() {
        given:
        InputStream io = new ClassPathResource("mtest-data/UserService-getUserById/testcase.csv").getInputStream()

        when:
        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(io)).build();

        List<TestCase> testcases = Lists.newArrayList();

        Iterator<String[]> iterator = csvReader.iterator();
        if (iterator.hasNext()) {
            //去除第一行的表头，从第二行开始
            iterator.next();
        }
        while (iterator.hasNext()) {
            String[] next = iterator.next();
            if (next==null || next.length==0) {
                continue;
            }
            TestCase testcase = new TestCase();
            testcase.setId(next[0]);
            testcase.setName(next[1]);
            if (next.length>=3 && next[2]!=null && (next[2].equals("1") || next[2].equals("ture"))) {
                continue;
            }

            testcases.add(testcase);
        }

        then:
        testcases != null
    }

}
