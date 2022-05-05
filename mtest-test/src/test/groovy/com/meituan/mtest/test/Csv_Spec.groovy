package com.meituan.mtest.test

import com.google.common.collect.Lists
import com.meituan.mtest.TestCase
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import spock.lang.Specification;

/**
 *
 * @author Jun Tan
 */
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
            boolean isException = false;
            if (next.length >= 3 && "1".equals(next[2]) || "ture".equals(next[2])) {
                isException = true;
            }
            if (next.length >= 4 && "1".equals(next[3]) || "ture".equals(next[3])) {
                continue;
            }
            testcases.add(new TestCase(next[0], next[1], isException));
        }

        then:
        testcases != null
    }

}
