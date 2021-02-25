package com.cevelop.templator.plugin.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public final class ReflectionMethodHelper {

    private ReflectionMethodHelper() {}

    public static Method getNonAccessibleMethod(Class<? extends Object> containingClass, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        Method relevantMethod = containingClass.getDeclaredMethod(methodName, parameterTypes);
        relevantMethod.setAccessible(true);

        return relevantMethod;
    }

    public static Field getNonAccessibleField(Class<? extends Object> containingClass, String fieldName) throws NoSuchFieldException,
            SecurityException {
        Field relevantField = containingClass.getDeclaredField(fieldName);
        relevantField.setAccessible(true);
        return relevantField;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStaticMethod(Method method, Object... arguments) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ClassCastException {
        method.setAccessible(true);
        Object result = method.invoke(null, arguments);
        return (T) result;
    }
}
