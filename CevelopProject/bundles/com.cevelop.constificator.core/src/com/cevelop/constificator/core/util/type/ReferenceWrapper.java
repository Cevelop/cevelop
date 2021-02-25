package com.cevelop.constificator.core.util.type;

public class ReferenceWrapper<T> {

    private T wrapped = null;

    public ReferenceWrapper() {}

    public ReferenceWrapper(T reference) {
        wrapped = reference;
    }

    public T get() {
        return wrapped;
    }

    public void set(T reference) {
        wrapped = reference;
    }

}
