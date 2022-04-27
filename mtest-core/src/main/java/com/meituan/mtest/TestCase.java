package com.meituan.mtest;

public class TestCase {

    private String code;

    private String name;

    private Mock[] mocks;

    public static class Mock {

        private String className;

        private String beanName;

        private String methodName;

        private String[] methodParameterTypes;

        private int order = -1;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String[] getMethodParameterTypes() {
            return methodParameterTypes;
        }

        public void setMethodParameterTypes(String[] methodParameterTypes) {
            this.methodParameterTypes = methodParameterTypes;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mock[] getMocks() {
        return mocks;
    }

    public void setMocks(Mock[] mocks) {
        this.mocks = mocks;
    }
}
