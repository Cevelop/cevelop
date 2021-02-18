package com.cevelop.includator.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;


@SuppressWarnings("restriction")
public class CDTInternalAccess {

    public static class CPPSemantics {

        public static ICPPConstructor findImplicitlyCalledConstructor(ICPPClassType type, IASTInitializer initializer, IASTNode typeId) {
            Method targetMethod;
            try {
                targetMethod = org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics.class.getDeclaredMethod(
                        "findImplicitlyCalledConstructor", ICPPClassType.class, IASTInitializer.class, IASTNode.class);
                targetMethod.setAccessible(true);
                Object result = targetMethod.invoke(null, type, initializer, typeId);
                return (ICPPConstructor) result;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Cannot access CDTs internal/private method 'CPPSemantics.findImplicitlyCalledConstructor");
        }
    }
}
