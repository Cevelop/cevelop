package com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;
import com.cevelop.gslator.utils.ASTHelper.SpecialFunction;


public class C35BaseClassDestructorVisitor extends BaseVisitor {

    public C35BaseClassDestructorVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            final IASTDeclaration destructor = ASTHelper.getFirstSpecialMemberFunction((ICPPASTCompositeTypeSpecifier) declSpec,
                    SpecialFunction.DefaultDestructor);
            for (final IASTDeclaration decl : ((ICPPASTCompositeTypeSpecifier) declSpec).getMembers()) {
                if (decl instanceof ICPPASTFunctionDefinition && nodeHasNoIgnoreAttribute(this, destructor) && destructor != null) {
                    final IASTDeclSpecifier declSpecifier = ((ICPPASTFunctionDefinition) decl).getDeclSpecifier();
                    if (declSpecifier != null) {
                        if (declSpecifier instanceof ICPPASTSimpleDeclSpecifier) {
                            ICPPASTSimpleDeclSpecifier declSpecOfFunction = (ICPPASTSimpleDeclSpecifier) declSpecifier;
                            if (hasVirtualMemberFunction(destructor, decl, declSpecOfFunction)) {
                                declSpecOfFunction = getSimpleDec(destructor);
                                if (isNotPublicVirtualOrPrivateNonVirtual(declSpecOfFunction, ASTHelper.getVisibilityForStatement(destructor))) {
                                    checker.reportProblem(ProblemId.P_C35, getDeclarator(destructor));
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.visit(declSpec);
    }

    private ICPPASTSimpleDeclSpecifier getSimpleDec(final IASTDeclaration decl) {
        if (decl instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition funcDec = (ICPPASTFunctionDefinition) decl;
            return (ICPPASTSimpleDeclSpecifier) funcDec.getDeclSpecifier();
        } else if (decl instanceof IASTSimpleDeclaration) {
            final IASTSimpleDeclaration simDec = (IASTSimpleDeclaration) decl;
            return (ICPPASTSimpleDeclSpecifier) simDec.getDeclSpecifier();
        }
        return null;
    }

    private ICPPASTDeclarator getDeclarator(final IASTDeclaration decl) {
        if (decl instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition funcDec = (ICPPASTFunctionDefinition) decl;
            return (ICPPASTDeclarator) funcDec.getDeclarator();
        } else if (decl instanceof IASTSimpleDeclaration) {
            final IASTSimpleDeclaration simDec = (IASTSimpleDeclaration) decl;
            return (ICPPASTDeclarator) simDec.getDeclarators()[0];
        }
        return null;
    }

    private boolean hasVirtualMemberFunction(final IASTDeclaration destructor, final IASTDeclaration decl,
            final ICPPASTSimpleDeclSpecifier declSpecOfFunction) {
        return declSpecOfFunction.isVirtual() && decl != destructor;
    }

    private boolean isNotPublicVirtualOrPrivateNonVirtual(final ICPPASTSimpleDeclSpecifier declSpecOfFunction, final int visibility) {
        return !(visibility == ICPPASTVisibilityLabel.v_public && declSpecOfFunction.isVirtual() ||
                 visibility == ICPPASTVisibilityLabel.v_protected && !declSpecOfFunction.isVirtual());
    }
}
