package com.cevelop.ctylechecker.domain.types.util;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPField;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.ctylechecker.domain.IConcept;


@SuppressWarnings("restriction")
public class TypeChecker {

    public Boolean isTypeApplicable(IBinding pResolvedBinding, IConcept pConcept) {
        if (pResolvedBinding instanceof CPPField) return isTypeApplicableToCppField((CPPField) pResolvedBinding, pConcept.getQualifiers());
        if (pResolvedBinding instanceof CPPVariable) return isTypeApplicableToCppVariable((CPPVariable) pResolvedBinding, pConcept.getQualifiers());
        if (pResolvedBinding instanceof CPPMethod) return isTypeApplicableToCppMethod((CPPMethod) pResolvedBinding, pConcept.getQualifiers());
        if (pResolvedBinding instanceof CPPFunction) return isTypeApplicableToCppFunction((CPPFunction) pResolvedBinding, pConcept.getQualifiers());
        return true;
    }

    private Boolean isTypeApplicableToCppMethod(CPPMethod pMethod, List<String> pQualifiers) {
        if (!isTypeApplicableToCppFunction(pMethod, pQualifiers)) {
            if (pMethod.isDestructor()) return pQualifiers.contains(Qualifiers.DESTRUCTOR);
            if (pMethod.isExplicit()) return pQualifiers.contains(Qualifiers.EXPLICIT);
            if (pMethod.isFinal()) return pQualifiers.contains(Qualifiers.FINAL);
            if (pMethod.isOverride()) return pQualifiers.contains(Qualifiers.OVERRIDE);
            if (pMethod.isPureVirtual()) return pQualifiers.contains(Qualifiers.PURE_VIRTUAL);
            if (pMethod.isImplicit()) return pQualifiers.contains(Qualifiers.IMPLICIT);
            if (pMethod.isVirtual()) return pQualifiers.contains(Qualifiers.VIRTUAL);
            if (pMethod.getVisibility() == CPPMethod.v_private) return pQualifiers.contains(Qualifiers.PRIVATE);
            if (pMethod.getVisibility() == CPPMethod.v_public) return pQualifiers.contains(Qualifiers.PUBLIC);
            if (pMethod.getVisibility() == CPPMethod.v_protected) return pQualifiers.contains(Qualifiers.PROTECTED);
        }
        return true;
    }

    private Boolean isTypeApplicableToCppFunction(CPPFunction pFunction, List<String> pQualifiers) {
        if (!pQualifiers.isEmpty()) {
            if (pFunction.isConstexpr()) return pQualifiers.contains(Qualifiers.CONSTEXPR);
            if (pFunction.isAuto()) return pQualifiers.contains(Qualifiers.AUTO);
            if (pFunction.isDeleted()) return pQualifiers.contains(Qualifiers.DELETED);
            if (pFunction.isExtern()) return pQualifiers.contains(Qualifiers.EXTERN);
            if (pFunction.isExternC()) return pQualifiers.contains(Qualifiers.EXTERN_C);
            if (pFunction.isInline()) return pQualifiers.contains(Qualifiers.INLINE);
            if (pFunction.isMutable()) return pQualifiers.contains(Qualifiers.MUTABLE);
            if (pFunction.isNoReturn()) return pQualifiers.contains(Qualifiers.NO_RETURN);
            if (pFunction.isRegister()) return pQualifiers.contains(Qualifiers.REGISTER);
            if (SemanticUtil.isConst(pFunction.getType())) return pQualifiers.contains(Qualifiers.CONST);
            return pQualifiers.contains(Qualifiers.DEFAULT);
        }
        return true;
    }

    private Boolean isTypeApplicableToCppField(CPPField pField, List<String> pQualifiers) {
        if (!isTypeApplicableToCppVariable(pField, pQualifiers)) {
            if (pField.getVisibility() == CPPField.v_private) return pQualifiers.contains(Qualifiers.PRIVATE);
            if (pField.getVisibility() == CPPField.v_protected) return pQualifiers.contains(Qualifiers.PROTECTED);
            if (pField.getVisibility() == CPPField.v_public) return pQualifiers.contains(Qualifiers.PUBLIC);
        }
        return true;
    }

    private Boolean isTypeApplicableToCppVariable(CPPVariable pVariable, List<String> pQualifiers) {
        if (!pQualifiers.isEmpty()) {
            if (SemanticUtil.isConst(pVariable.getType())) return pQualifiers.contains(Qualifiers.CONST);
            if (pVariable.isAuto()) return pQualifiers.contains(Qualifiers.AUTO);
            if (pVariable.isStatic()) return pQualifiers.contains(Qualifiers.STATIC);
            if (pVariable.isConstexpr()) return pQualifiers.contains(Qualifiers.CONSTEXPR);
            if (pVariable.isMutable()) return pQualifiers.contains(Qualifiers.MUTABLE);
            if (pVariable.isExtern()) return pQualifiers.contains(Qualifiers.EXTERN);
            if (pVariable.isExternC()) return pQualifiers.contains(Qualifiers.EXTERN_C);
            return pQualifiers.contains(Qualifiers.DEFAULT);
        }
        return true;
    }
}
