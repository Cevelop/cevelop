package com.cevelop.conanator.tests.parser.support;

import java.lang.reflect.Field;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;


public class ParserTestRunner extends BlockJUnit4ClassRunner {

    private String fSourceText;
    private Field  fSourceTextField;

    public ParserTestRunner(Class<?> klass) throws Exception {
        super(klass);
        Class<?> base = findParserTestBase(klass);
        fSourceTextField = base.getDeclaredField("fSourceText");
        fSourceTextField.setAccessible(true);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        fSourceTextField.set(test, fSourceText);
        return test;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        Conanfile conanfile = method.getAnnotation(Conanfile.class);
        if (conanfile == null) {
            throw new IllegalArgumentException("Test method is missing @Conanfile annotation");
        }

        fSourceText = conanfile.value();
        super.runChild(method, notifier);
    }

    private Class<?> findParserTestBase(Class<?> klass) throws InitializationError {
        final Class<?> originalKlass = klass;

        while (klass != null && klass != ParserTest.class) {
            klass = klass.getSuperclass();
        }

        if (klass == null) {
            throw new InitializationError(originalKlass.getName() + " is not derived from " + ParserTest.class.getName());
        }

        return klass;
    }

}
