package com.meituan.mtest;

import com.google.common.collect.Maps;

import java.util.*;

/**
 *
 * @author Jun Tan
 */
public class MTestContext implements Map<String, Object> {

    private ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    /**
     *
     */
    private MTestContext() {
        context.set(Maps.newHashMap());
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
        return (TestCase) context.get().get(KeyType.TEST_CASE.key);
    }

    /**
     *
     * @return
     */
    public Object[] getRequest() {
        return (Object[]) context.get().get(KeyType.REQUEST.key);
    }

    /**
     *
     * @return
     */
    public Object getExpected() {
        return context.get().get(KeyType.EXPECTED.key);
    }

    /**
     *
     * @return
     */
    public Throwable getException() {
        return (Throwable) context.get().get(KeyType.EXCEPTION.key);
    }

    /**
     *
     */
    public void cleanup() {
        context.get().clear();
    }

    @Override
    public int size() {
        return context.get().size();
    }

    @Override
    public boolean isEmpty() {
        return context.get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return context.get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return context.get().containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return context.get().get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return context.get().put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return context.get().remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        context.get().putAll(m);
    }

    @Override
    public void clear() {
        context.get().clear();
    }

    @Override
    public Set<String> keySet() {
        return context.get().keySet();
    }

    @Override
    public Collection<Object> values() {
        return context.get().values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return context.get().entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return context.get().equals(o);
    }

    @Override
    public int hashCode() {
        return context.get().hashCode();
    }

    /**
     *
     */
    public enum KeyType {
        /** TEST_CASE */
        TEST_CASE("testCase"),
        /** REQUEST */
        REQUEST("request"),
        /** EXPECTED */
        EXPECTED("expected"),
        /** EXCEPTION */
        EXCEPTION("exception"),
        ;

        private String key;

        KeyType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
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
                    context.put(keyType.getKey(), value);
                    return value;
                }
            };
        }
    }

}
