package com.cevelop.codeanalysator.autosar.util;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.VariableHelpers;


public final class InterfaceHelper {

    /***
     * Finds out whether a given basespecifier is an interface.
     * A class is an interface class if there are only
     * public pure virtual methods and public static constexpr data members.
     *
     * @param baseSpec
     * the base specifier
     * @return whether the class is an interface
     */
    public static boolean isInterface(ICPPASTBaseSpecifier baseSpec) {
        IASTName name = (IASTName) baseSpec.getNameSpecifier();
        IBinding binding = name.resolveBinding();
        if (binding instanceof ICPPClassType) {
            ICPPClassType classType = (ICPPClassType) binding;
            return isInterface(classType);
        }
        return false;
    }

    /***
     * Finds out whether a given ICPPClassType is an interface.
     * A class is an interface class if there are only
     * public pure virtual methods and public static constexpr data members.
     *
     * @param classType
     * the class type for which to determine if it is an interface
     * @return whether the class is an interface
     */
    public static boolean isInterface(ICPPClassType classType) {
        ICPPMethod[] methods = classType.getDeclaredMethods();
        ICPPField[] fields = classType.getDeclaredFields();

        for (ICPPMethod method : methods) {
            if (!method.isPureVirtual()) {
                return false;
            }
            if (method.getVisibility() != ICPPASTVisibilityLabel.v_public) {
                return false;
            }
        }

        for (ICPPField field : fields) {
            if (field.getVisibility() != ICPPASTVisibilityLabel.v_public) {
                return false;
            }
            if (!field.isStatic()) {
                return false;
            }
            if (!isConstExpression(field)) {
                return false;
            }
        }
        return true;
    }

    /***
     * Needs to use discouraged access on CPPVariable.
     * Otherwise isConstexpr() will fail, because the fDefinition is null and getDeclarations is not accessible from ICPPField.
     *
     * @param field
     * @return
     */
    @SuppressWarnings("restriction")
    private static boolean isConstExpression(ICPPField field) {
        if (field != null && field instanceof CPPVariable) {
            CPPVariable variable = (CPPVariable) field;
            IASTName[] declarations = variable.getDeclarations();
            if (declarations != null && declarations.length != 0) {
                return VariableHelpers.isConstexpr(declarations[0].getLastName());
            }
        }
        return false;
    }
}
