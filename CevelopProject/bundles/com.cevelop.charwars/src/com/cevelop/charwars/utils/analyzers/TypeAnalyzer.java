package com.cevelop.charwars.utils.analyzers;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;

import com.cevelop.charwars.constants.StdString;


public class TypeAnalyzer {

    public static boolean isStdStringType(IType type) {
        return isConstStdStringReference(type) || isStdString(type);
    }

    public static boolean isConstStdStringReference(IType type) {
        String className = getClassName(getConstQualifiedType(getReferencedType(type)));
        return isValidClassName(className);
    }

    public static boolean isStdString(IType type) {
        String className = getClassName(type);
        return isValidClassName(className);
    }

    public static boolean isCStringType(IType type, boolean isConst) {
        IType t = getPointeeType(type);
        if (isConst) {
            t = getConstQualifiedType(t);
        }
        IBasicType.Kind basicKind = getBasicKind(t);
        return basicKind == Kind.eChar || basicKind == Kind.eWChar;
    }

    private static IType getPointeeType(IType type) {
        IType normalized = normalize(type);
        if (normalized instanceof IPointerType) {
            IPointerType pointerType = (IPointerType) type;
            return normalize(pointerType.getType());
        }
        return null;
    }

    public static boolean matchingTypes(IType type1, IType type2) {
        final String pattern = "<.*>";
        String type1Str = ASTTypeUtil.getType(type1).replaceAll(pattern, "");
        String type2Str = ASTTypeUtil.getType(type2).replaceAll(pattern, "");
        return type1Str.equals(type2Str);
    }

    public static IBasicType.Kind getBasicKind(IType type) {
        IType normalized = normalize(type);
        if (normalized instanceof IBasicType) {
            IBasicType basicType = (IBasicType) normalized;
            return basicType.getKind();
        }
        return IBasicType.Kind.eUnspecified;
    }

    private static IType getReferencedType(IType type) {
        IType normalized = normalize(type);
        if (normalized instanceof ICPPReferenceType) {
            ICPPReferenceType referenceType = (ICPPReferenceType) normalized;
            return normalize(referenceType.getType());
        }
        return null;
    }

    private static IType getConstQualifiedType(IType type) {
        IType normalized = normalize(type);
        if (normalized instanceof IQualifierType) {
            IQualifierType qualifierType = (IQualifierType) normalized;
            if (qualifierType.isConst()) {
                return normalize(qualifierType.getType());
            }
        }
        return null;
    }

    private static String getClassName(IType type) {
        IType normalized = normalize(type);
        if (normalized instanceof ICPPClassType) {
            ICPPClassType classType = (ICPPClassType) normalized;
            return classType.getName();
        }
        return null;
    }

    private static boolean isValidClassName(String className) {
        return StdString.STRING.equals(className) || StdString.BASIC_STRING.equals(className);
    }

    private static IType normalize(IType type) {
        IType normalizedType = type;
        if (type instanceof ICPPClassSpecialization) {
            ICPPClassSpecialization classSpecialization = (ICPPClassSpecialization) type;
            normalizedType = classSpecialization.getSpecializedBinding();
        } else if (type instanceof ITypedef) {
            ITypedef typedef = (ITypedef) type;
            normalizedType = typedef.getType();
        }
        return normalizedType;
    }
}
