package com.meituan.mtest;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

public class MtestContext {

    private static ThreadLocal<TestCase> testCase = new ThreadLocal<>();
    private static ThreadLocal<Object[]> request = new ThreadLocal<>();
    private static ThreadLocal<Object> expected = new ThreadLocal<>();
    private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    public static TestCase getTestCase() {
        return testCase.get();
    }

    public static Object[] getRequest() {
        return request.get();
    }

    public static Object getExpected() {
        return expected.get();
    }

    public static Object get(String key) {
        if (context.get()!=null) {
            return context.get().get(key);
        } else {
            return null;
        }
    }

    public void put(String key, Object object) {
        if (context.get()==null) {
            context.set(Maps.newHashMap());
        }
        context.get().put(key, object);
    }

    public void cleanup() {
        testCase.remove();
        request.remove();
        expected.remove();
        context.remove();
    }

    public static enum KeyType {
        TEST_CASE,
        REQUEST,
        EXPECTED,
    }

    public static class ContextIterable implements Iterable {

        private Iterable iterable;
        private KeyType keyType;

        private ContextIterable(Iterable iterable, KeyType keyType) {
            this.iterable = iterable;
            this.keyType = keyType;
        }

        public static ContextIterable of(Iterable iterable, KeyType keyType) {
            return new ContextIterable(iterable, keyType);
        }

        @Override
        public Iterator iterator() {
            Iterator iterator = iterable.iterator();
            return new Iterator() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Object next() {
                    Object value = iterator.next();
                    switch (keyType){
                        case TEST_CASE:
                            MtestContext.testCase.set((TestCase) value);
                            break;
                        case REQUEST:
                            MtestContext.request.set((Object[]) value);
                            break;
                        case EXPECTED:
                            MtestContext.expected.set(value);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + keyType);
                    }
                    return value;
                }
            };
        }
    }

}
