package com.cevelop.constificator.core.util.type;

public class Cast {

    @SuppressWarnings("unchecked")
    public static <T> T as(Class<T> cls, Object node) {
        if (cls.isInstance(node)) {
            return (T) node;
        }

        return null;
    }

}
