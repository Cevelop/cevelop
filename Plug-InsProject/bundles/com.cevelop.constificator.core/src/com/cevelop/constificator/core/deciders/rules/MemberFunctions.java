package com.cevelop.constificator.core.deciders.rules;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.constificator.core.util.semantic.MemberFunction;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.Pair;


public class MemberFunctions {

    public static Pair<Boolean, Boolean> constOverloadExists(ICPPASTFunctionDeclarator member) {
        ICPPASTName name;
        if (member == null || (name = Cast.as(ICPPASTName.class, member.getName())) == null) {
            throw new NullPointerException("The supplied declarator must not be null!");
        }

        Pair<Boolean, Boolean> overloadDescription = new Pair<>(false, false);

        ICPPMethod binding;
        if ((binding = Cast.as(ICPPMethod.class, name.resolveBinding())) == null) {
            return overloadDescription;
        }

        ICPPClassType owningClass;
        if ((owningClass = Cast.as(ICPPClassType.class, binding.getOwner())) == null) {
            return overloadDescription;
        }

        ICPPMethod[] methods = owningClass.getAllDeclaredMethods();

        IType type = binding.getType();
        String idxName = binding.getName();

        for (ICPPMethod method : methods) {
            if (method.getName().equals(idxName) && Type.areSameTypeIgnoringConst(method.getType(), type)) {
                if (Type.isConst(method.getType(), 0)) {
                    overloadDescription.first(true);
                }
            }
        }

        if (MemberFunction.shadows(binding, member) || MemberFunction.overrides(binding, member)) {
            overloadDescription.second(true);
        }

        return overloadDescription;
    }

    public static boolean isConstructorOrDestructor(ICPPASTFunctionDeclarator function) {
        if (function == null) {
            return false;
        }

        ICPPASTName name;
        if ((name = Cast.as(ICPPASTName.class, function.getName())) == null) {
            return false;
        }

        ICPPMethod method;
        if ((method = Cast.as(ICPPMethod.class, name.resolveBinding())) == null) {
            return false;
        }

        return method instanceof ICPPConstructor || method.isDestructor();
    }

    public static ICPPMethod[] memberFunctionsForOwnerOf(ICPPMethod method) {
        if (method == null) {
            return new ICPPMethod[] {};
        }

        ICPPClassType cls = method.getClassOwner();
        ICPPMethod[] methods = cls instanceof ICPPClassType ? cls.getAllDeclaredMethods() : cls.getAllDeclaredMethods();
        return methods;
    }

}
