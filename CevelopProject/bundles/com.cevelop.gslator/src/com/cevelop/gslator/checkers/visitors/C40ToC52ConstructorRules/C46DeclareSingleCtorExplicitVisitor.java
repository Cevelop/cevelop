package com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C46DeclareSingleCtorExplicitVisitor extends BaseVisitor {

    public C46DeclareSingleCtorExplicitVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (declarator instanceof ICPPASTFunctionDeclarator && ASTHelper.getCompositeTypeSpecifier(declarator) != null && nodeHasNoIgnoreAttribute(
                this, declarator)) {

            final ICPPASTFunctionDeclarator funcDecl = (ICPPASTFunctionDeclarator) declarator;
            final IASTName funcName = funcDecl.getName();
            if (funcName == null) {
                return PROCESS_CONTINUE;
            }
            final IBinding binding = funcName.resolveBinding();

            if (!(binding instanceof ICPPConstructor)) {
                return PROCESS_CONTINUE;
            }

            if (validate((ICPPConstructor) binding)) {
                checker.reportProblem(ProblemId.P_C46, funcDecl);
            }
        }

        return super.visit(declarator);
    }

    private boolean validate(final ICPPConstructor constructor) {

        return !isExplicit(constructor) && !isPrivate(constructor) && !isSpecialMember(constructor) && hasSingleNonDefaultedParameter(constructor);
    }

    private static boolean isExplicit(final ICPPConstructor constructor) {
        return constructor.isExplicit();
    }

    private static boolean isPrivate(final ICPPConstructor constructor) {
        return constructor.getVisibility() == ICPPMember.v_private;
    }

    private static boolean isSpecialMember(final ICPPConstructor constructor) {
        final ICPPParameter[] parameters = constructor.getParameters();

        if (parameters.length == 0 || parameters[0].getDefaultValue() != null) {
            return true;
        }

        if (parameters.length > 1 && parameters[1].getDefaultValue() == null) {
            return false;
        }

        final IType parameterType = parameters[0].getType();

        final ICPPClassType constructorType = constructor.getClassOwner();

        if (parameterType instanceof IBasicType) {
            return ((IBasicType) parameterType).getKind() == IBasicType.Kind.eVoid;
        }

        if (parameterType instanceof ICPPClassType) {
            return constructorType.isSameType(parameterType);
        }

        if (parameterType instanceof ICPPReferenceType) {
            final IType referenceType = ((ICPPReferenceType) parameterType).getType();

            if (referenceType instanceof IQualifierType) {
                return constructorType.isSameType(((IQualifierType) referenceType).getType());
            }

            if (referenceType instanceof ICPPClassType) {
                return constructorType.isSameType(referenceType);
            }
        }

        return false;
    }

    private static boolean hasSingleNonDefaultedParameter(final ICPPConstructor constructor) {
        final ICPPParameter[] parameters = constructor.getParameters();
        return parameters.length == 1 || parameters.length > 1 && parameters[1].getDefaultValue() != null;
    }
}
