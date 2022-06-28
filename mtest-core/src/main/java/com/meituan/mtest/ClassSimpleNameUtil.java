package com.meituan.mtest;

/**
 *
 * @author Jun Tan
 */
public class ClassSimpleNameUtil {

    /**
     *
     * @param clazz
     * @return
     */
    public static String getSimpleName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (enclosingClass == null) {
            // top level class
            return clazz.getSimpleName();
        }
        // Otherwise, strip the package name
        try {
            return clazz.getName().substring(clazz.getName().lastIndexOf(".")+1);
        } catch (IndexOutOfBoundsException ex) {
            throw new InternalError("Malformed class name", ex);
        }
    }

}
