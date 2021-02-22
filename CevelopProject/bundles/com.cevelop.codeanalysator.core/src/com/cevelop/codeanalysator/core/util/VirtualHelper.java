package com.cevelop.codeanalysator.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.parser.util.ArrayUtil;


public class VirtualHelper {

    /**
     * Looks up all base classes for given class type recursively
     *
     * @param classType
     * for which to find base classes
     * @return Array of base classes
     */
    public static ICPPClassType[] getAllBases(ICPPClassType classType) {
        Set<ICPPClassType> result = new HashSet<>();
        result.add(classType);
        getAllBases(classType, result);
        result.remove(classType);
        return result.toArray(new ICPPClassType[result.size()]);
    }

    /**
     * Finds out whether a functionDeclarator is a virtual method.
     * This can either be because of the virtual keyword or
     * because it overrides a method in a base class.
     *
     * @param functionDeclarator
     * for which to check
     * @return whether the functionDeclarator is virtual
     */
    public static boolean isVirtualMethod(ICPPASTFunctionDeclarator functionDeclarator) {
        IASTName name = functionDeclarator.getName();
        if (name != null) {
            IBinding binding = name.resolveBinding();
            if (binding instanceof ICPPMethod) {
                ICPPMethod method = (ICPPMethod) binding;
                //This does not recognize when a method is virtual because it overrides a member of a base class
                if (method.isVirtual()) {
                    return !functionDeclarator.isFinal();
                } else if (functionDeclarator.isFinal()) {
                    return false;
                }
                if (overridesVirtualMethod(method)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the innermost nested declarator
     *
     * @param declarator
     * for which to search
     * @return the innermost declarator
     */
    public static IASTDeclarator findInnermostDeclarator(IASTDeclarator declarator) {
        IASTDeclarator innermost = null;
        while (declarator != null) {
            innermost = declarator;
            declarator = declarator.getNestedDeclarator();
        }
        return innermost;
    }

    /**
     * Finds out whether a given method shadows another method in a base class
     *
     * @param testedMethod
     * the method to test shadowing for
     * @return whether the method shadows another
     * @throws DOMException
     * a DOM exception
     */
    public static boolean shadows(ICPPMethod testedMethod) throws DOMException {
        final String testedMethodName = testedMethod.getName();
        HashMap<ICPPClassType, ICPPMethod[]> methodsCache = new HashMap<>();
        ICPPClassType containingClass = testedMethod.getClassOwner();
        ICPPMethod[] allInheritedMethods;
        if (methodsCache.containsKey(containingClass)) {
            allInheritedMethods = methodsCache.get(containingClass);
        } else {
            ICPPMethod[] inheritedMethods = null;
            ICPPClassType[] bases = VirtualHelper.getAllBases(containingClass);
            for (ICPPClassType base : bases) {
                inheritedMethods = ArrayUtil.addAll(ICPPMethod.class, inheritedMethods, VirtualHelper.getAllDeclaredMethods(base));
            }
            allInheritedMethods = ArrayUtil.trim(ICPPMethod.class, inheritedMethods);
            methodsCache.put(containingClass, allInheritedMethods);
        }

        for (ICPPMethod method : allInheritedMethods) {
            if (method.getName().equals(testedMethodName)) {
                if (!method.isVirtual() && !VirtualHelper.isOverrider(testedMethod, method)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Tests whether the source is an overrider of target
     *
     * @param source
     * potentially overriding method
     * @param target
     * potentially overridden method
     * @return whether the source is an overrider of target
     */
    public static boolean isOverrider(ICPPMethod source, ICPPMethod target) {
        if (source instanceof ICPPConstructor || target instanceof ICPPConstructor) {
            return false;
        }
        if (!target.isVirtual() && !overridesVirtualMethod(target)) {
            return false;
        }
        if (!functionTypesAllowOverride(source.getType(), target.getType())) {
            return false;
        }

        final ICPPClassType sourceClass = source.getClassOwner();
        final ICPPClassType targetClass = target.getClassOwner();
        if (sourceClass == null || targetClass == null) {
            return false;
        }

        ICPPClassType[] bases = getAllBases(sourceClass);
        for (ICPPClassType base : bases) {
            if (base.isSameType(targetClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tests whether a virtual method overrides any other
     *
     * @param method
     * to be tested
     * @return whether a virtual method overrides any other
     */
    public static boolean overridesVirtualMethod(ICPPMethod method) {
        if (method.isDestructor()) {
            ICPPClassType classtype = method.getClassOwner();
            if (classtype == null) {
                return false;
            }
            ICPPClassType[] bases = getAllBases(classtype);
            if (bases.length < 1) {
                return false;
            }
            for (ICPPClassType type : bases) {
                for (ICPPMethod meth : type.getAllDeclaredMethods()) {
                    if (meth.isVirtual() && meth.isDestructor()) {
                        return true;
                    }
                }
            }
            return false;
        }
        ICPPClassType[] bases = VirtualHelper.getAllBases(method.getClassOwner());
        if (bases.length < 1) {
            return false;
        }

        boolean overrides = false;
        try {
            overrides = overrides(method);
        } catch (DOMException e) {
            e.printStackTrace();
        }
        return overrides;
    }

    /***
     * counts the number of virtspecifiers. Virt specifiers are virtual, override and final
     *
     * @param declarator
     * the function declarator
     * @return the number of virtual specifiers
     */
    public static int countNumberOfVirtSpecifiers(ICPPASTFunctionDeclarator declarator) {
        int numberOfVirtSpecs = 0;
        numberOfVirtSpecs += declarator.getVirtSpecifiers().length;

        IASTName name = declarator.getName();
        if (name != null) {
            IBinding binding = name.resolveBinding();
            if (binding instanceof ICPPMethod) {
                ICPPMethod method = (ICPPMethod) binding;
                if (method.isVirtual()) {
                    numberOfVirtSpecs++;
                }
            }
        }
        return numberOfVirtSpecs;
    }

    private static boolean overrides(ICPPMethod testedMethod) throws DOMException {
        final String testedMethodName = testedMethod.getName();
        HashMap<ICPPClassType, ICPPMethod[]> methodsCache = new HashMap<>();
        ICPPClassType aClass = testedMethod.getClassOwner();

        ICPPMethod[] allInheritedMethods;
        if (methodsCache.containsKey(aClass)) {
            allInheritedMethods = methodsCache.get(aClass);
        } else {
            ICPPMethod[] inheritedMethods = null;
            ICPPClassType[] bases = VirtualHelper.getAllBases(aClass);
            for (ICPPClassType base : bases) {
                inheritedMethods = ArrayUtil.addAll(ICPPMethod.class, inheritedMethods, VirtualHelper.getAllDeclaredMethods(base));
            }
            allInheritedMethods = ArrayUtil.trim(ICPPMethod.class, inheritedMethods);
            methodsCache.put(aClass, allInheritedMethods);
        }

        for (ICPPMethod method : allInheritedMethods) {
            if (method.getName().equals(testedMethodName)) {
                if (method.isVirtual()) {
                    return true;
                }

                boolean overrides = VirtualHelper.isOverrider(testedMethod, method);
                return overrides;
            }
        }
        return false;
    }

    private static void getAllBases(ICPPClassType classType, Set<ICPPClassType> result) {
        ICPPBase[] bases = classType.getBases();
        for (ICPPBase base : bases) {
            IBinding b = base.getBaseClass();
            if (b instanceof ICPPClassType) {
                final ICPPClassType baseClass = (ICPPClassType) b;
                if (result.add(baseClass)) {
                    getAllBases(baseClass, result);
                }
            }
        }
    }

    private static ICPPMethod[] getAllDeclaredMethods(ICPPClassType ct) {
        ICPPMethod[] methods = ct.getDeclaredMethods();
        ICPPClassType[] bases = getAllBases(ct);
        for (ICPPClassType base : bases) {
            methods = ArrayUtil.addAll(ICPPMethod.class, methods, base.getDeclaredMethods());
        }
        return ArrayUtil.trim(ICPPMethod.class, methods);
    }

    private static boolean functionTypesAllowOverride(ICPPFunctionType a, ICPPFunctionType b) {
        if (a.isConst() != b.isConst() || a.isVolatile() != b.isVolatile() || a.takesVarArgs() != b.takesVarArgs()) {
            return false;
        }

        IType[] paramsA = a.getParameterTypes();
        IType[] paramsB = b.getParameterTypes();

        if (paramsA.length == 1 && paramsB.length == 0) {
            if (!isVoidType(paramsA[0])) {
                return false;
            }
        } else if (paramsB.length == 1 && paramsA.length == 0) {
            if (!isVoidType(paramsB[0])) {
                return false;
            }
        } else if (paramsA.length != paramsB.length) {
            return false;
        } else {
            for (int i = 0; i < paramsA.length; i++) {
                if (paramsA[i] == null || !paramsA[i].isSameType(paramsB[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isVoidType(IType ptype) {
        while (ptype instanceof ITypedef) {
            ptype = ((ITypedef) ptype).getType();
        }
        if (ptype instanceof IBasicType) {
            return ((IBasicType) ptype).getKind() == Kind.eVoid;
        }
        return false;
    }
}
