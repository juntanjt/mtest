package com.meituan.mtest;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Jun Tan
 */
public class MTestContext {

    private ThreadLocal<TestCase> testCase = new ThreadLocal<>();
    private ThreadLocal<Object[]> request = new ThreadLocal<>();
    private ThreadLocal<Object> expected = new ThreadLocal<>();
    private ThreadLocal<Throwable> exception = new ThreadLocal<>();
    private ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    /**
     *
     */
    private MTestContext() {
    }

    /**
     *
     * @return
     */
    public static MTestContext newInstance() {
        return new MTestContext();
    }

    /**
     *
     * @return
     */
    public TestCase getTestCase() {
        return testCase.get();
    }

    /**
     *
     * @return
     */
    public Object[] getRequest() {
        return request.get();
    }

    /**
     *
     * @return
     */
    public Object getExpected() {
        return expected.get();
    }

    public Throwable getException() {
        return exception.get();
    }

    /**
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        if (context.get()!=null) {
            return context.get().get(key);
        } else {
            return null;
        }
    }

    /**
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {
        if (context.get()==null) {
            context.set(Maps.newHashMap());
        }
        context.get().put(key, object);
    }

    /**
     *
     */
    public void cleanup() {
        testCase.remove();
        request.remove();
        expected.remove();
        exception.remove();
        context.remove();
    }

    /**
     *
     */
    public enum KeyType {
        /** TEST_CASE */
        TEST_CASE,
        /** REQUEST */
        REQUEST,
        /** EXPECTED */
        EXPECTED,
        /** EXCEPTION */
        EXCEPTION,
    }

    /**
     *
     */
    public static class ContextIterable implements Iterable {

        private Iterable iterable;
        private MTestContext context;
        private KeyType keyType;

        /**
         *
         * @param iterable
         * @param context
         * @param keyType
         */
        private ContextIterable(Iterable iterable, MTestContext context, KeyType keyType) {
            this.iterable = iterable;
            this.context = context;
            this.keyType = keyType;
        }

        /**
         *
         * @param iterable
         * @param context
         * @param keyType
         * @return
         */
        public static ContextIterable of(Iterable iterable, MTestContext context, KeyType keyType) {
            return new ContextIterable(iterable, context, keyType);
        }

        /**
         *
         * @return
         */
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
                            context.testCase.set((TestCase) value);
                            break;
                        case REQUEST:
                            context.request.set((Object[]) value);
                            break;
                        case EXPECTED:
                            context.expected.set(value);
                            break;
                        case EXCEPTION:
                            context.exception.set((Throwable) value);
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
