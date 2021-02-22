package com.cevelop.tdd.checkers.visitors;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPDeferredClassInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownMemberClass;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.ConstructorInfo;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class MissingConstructorVisitor extends ASTVisitor {

    {
        shouldVisitDeclarations = true;
    }

    private Consumer3<IProblemId<ProblemId>, IASTName, ConstructorInfo> problemReporter;

    public MissingConstructorVisitor(Consumer3<IProblemId<ProblemId>, IASTName, ConstructorInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration && !isMemberDeclaration(declaration)) {
            IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) declaration;
            IASTDeclSpecifier typespec = simpleDecl.getDeclSpecifier();
            final int storageClass = typespec.getStorageClass();
            if (storageClass == IASTDeclSpecifier.sc_typedef || storageClass == IASTDeclSpecifier.sc_extern) {
                return PROCESS_CONTINUE;
            }
            if (typespec instanceof IASTNamedTypeSpecifier) {
                IASTNamedTypeSpecifier namedTypespec = (IASTNamedTypeSpecifier) typespec;
                handleNamedTypeSpecifier(simpleDecl, namedTypespec);
            } else if (typespec instanceof IASTCompositeTypeSpecifier) {
                final IASTCompositeTypeSpecifier typeDefinition = (IASTCompositeTypeSpecifier) typespec;
                handleCompositeTypeSpecifier(simpleDecl, typeDefinition);
            }
        }
        return PROCESS_CONTINUE;
    }

    private void handleCompositeTypeSpecifier(IASTSimpleDeclaration simpleDecl, final IASTCompositeTypeSpecifier typeDefinition) {
        final IASTName typeName = typeDefinition.getName();
        checkAndReportUnresolvableConstructors(simpleDecl, typeName.toString());
    }

    private void handleNamedTypeSpecifier(IASTSimpleDeclaration simpleDecl, IASTNamedTypeSpecifier namedTypespec) {
        final IBinding typeBinding = namedTypespec.getName().resolveBinding();
        if (typeBinding instanceof ICPPDeferredClassInstance) { // Workaround fix, as constructors of templates cannot be resolved correctly
            return;
        }
        if (isConstructibleType(typeBinding)) {
            String typeName = ASTTypeUtil.getType(SemanticUtil.getSimplifiedType((IType) typeBinding), false);
            if (!isReferenceType(typeName)) {
                typeName = stripTemplateArguments(typeName);
                checkAndReportUnresolvableConstructors(simpleDecl, typeName);
            }
        }
    }

    private boolean isReferenceType(String typeName) {
        return typeName.contains("&") || typeName.contains("*");
    }

    private String stripTemplateArguments(String typeName) {
        return typeName.replaceAll("<.*", "");
    }

    private boolean isMemberDeclaration(IASTDeclaration declaration) {
        return declaration.getParent() instanceof IASTCompositeTypeSpecifier;
    }

    private boolean isConstructibleType(IBinding typeBinding) {
        if (typeBinding instanceof IProblemBinding) {
            return false;
        }
        if (typeBinding instanceof IType) {
            IType type = (IType) typeBinding;
            return isConstructibleType(type);
        }
        return false;
    }

    private boolean isConstructibleType(IType typeBinding) {
        IType bareType = TypeHelper.windDownToRealType(typeBinding, false);
        if (bareType instanceof IEnumeration) {
            return false;
        } else if (bareType instanceof IBasicType) {
            return false;
        } else if (bareType instanceof ICompositeType && !(bareType instanceof ICPPClassType)) {
            return false;
        } else if (bareType instanceof ICPPTemplateParameter) {
            return false;
        } else if (bareType instanceof ICPPUnknownMemberClass) {
            return false;
        }
        return true;
    }

    private void checkAndReportUnresolvableConstructors(IASTSimpleDeclaration simpledec, String typename) {
        for (IASTDeclarator ctorDecl : simpledec.getDeclarators()) {
            boolean hasPointerOrRefType = TddHelper.hasPointerOrRefType(ctorDecl);
            boolean isFunctionDeclarator = ctorDecl instanceof IASTFunctionDeclarator;
            if (!hasPointerOrRefType && !isFunctionDeclarator && hasCtorInitializer(ctorDecl)) {
                if (!isConstructorAvailable(ctorDecl)) {
                    reportMemberProblem(typename, ctorDecl.getName());
                }
            }
        }
    }

    private void reportMemberProblem(String missingName, IASTName name) {
        ConstructorInfo info = new ConstructorInfo();
        info.typeName = missingName;
//        info.message = missingName;
        problemReporter.accept(ProblemId.MISSING_CONSTRUCTOR, name, info);
    }

    private boolean isConstructorAvailable(IASTDeclarator ctorDecl) {
        if (!(ctorDecl instanceof IASTImplicitNameOwner)) {
            return false;
        }
        IASTImplicitNameOwner implNameOwner = (IASTImplicitNameOwner) ctorDecl;
        IASTImplicitName[] implicitNames = implNameOwner.getImplicitNames();
        return implicitNames.length > 0 && !(implicitNames[0].getBinding() instanceof IProblemBinding);
    }

    private boolean hasCtorInitializer(IASTDeclarator ctorDecl) {
        IASTInitializer initializer = ctorDecl.getInitializer();
        // FIXME: now really? method hasXY return true if the thing is null? either the method name is crap or there is a bug for sure here
        // (lfelber)
        // FIXME: One upon a time, we tried to handle braced initialization... We failed... This was part of the return statement:
        //  || (initializer instanceof ICPPASTInitializerList && !isAggregateInitializable(ctorDecl));
        return initializer == null || initializer instanceof ICPPASTConstructorInitializer;
    }
}
