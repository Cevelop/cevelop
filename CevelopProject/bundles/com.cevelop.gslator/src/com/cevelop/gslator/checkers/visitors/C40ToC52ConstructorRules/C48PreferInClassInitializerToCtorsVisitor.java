
package com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C48PreferInClassInitializerToCtorsVisitor extends BaseVisitor {

    public C48PreferInClassInitializerToCtorsVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    public void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            final ICPPASTCompositeTypeSpecifier struct = (ICPPASTCompositeTypeSpecifier) declSpec;
            final List<IASTSimpleDeclaration> memVars = ASTHelper.collectMemberVariables(struct);
            final ICPPASTFunctionDeclarator complexCtor = getCtorWithMostParams(struct);

            if (complexCtor != null && nodeHasNoIgnoreAttribute(this, complexCtor) && ctorInitializesAllMemVars(complexCtor, memVars) &&
                allParamsHaveDefaultValues(complexCtor, memVars)) {
                checker.reportProblem(ProblemId.P_C48, complexCtor);
            }
        }

        return super.visit(declSpec);
    }

    private boolean ctorInitializesAllMemVars(final ICPPASTFunctionDeclarator complexCtor, final List<IASTSimpleDeclaration> memVars) {
        final IASTNode parent = complexCtor.getParent();
        ICPPASTConstructorChainInitializer ctorInits[] = null;
        if (parent instanceof ICPPASTFunctionDefinition) {
            ctorInits = ((ICPPASTFunctionDefinition) parent).getMemberInitializers();
        } else if (parent instanceof IASTSimpleDeclaration) {
            final ICPPASTFunctionDefinition funcDefImpl = getImplFromDeclaration((IASTSimpleDeclaration) parent);
            if (funcDefImpl != null) {
                ctorInits = funcDefImpl.getMemberInitializers();
            }
        }
        return ctorInits != null ? ctorInits.length == countDeclarators(memVars) : false;
    }

    private int countDeclarators(final List<IASTSimpleDeclaration> memVars) {
        int count = 0;
        for (final IASTSimpleDeclaration simDec : memVars) {
            count += simDec.getDeclarators().length;
        }
        return count;
    }

    private boolean allParamsHaveDefaultValues(final ICPPASTFunctionDeclarator complexCtor, final List<IASTSimpleDeclaration> memVars) {
        if (countDeclarators(memVars) == 0) {
            return false;
        }
        for (final IASTSimpleDeclaration var : memVars) {
            if (!paramIsDefaulted(var, complexCtor)) {
                return false;
            }
        }
        return true;
    }

    private boolean paramIsDefaulted(final IASTSimpleDeclaration var, final ICPPASTFunctionDeclarator complexCtor) {
        final IASTNode parent = complexCtor.getParent();
        final ICPPASTFunctionDefinition funcDef = getFuncDef(parent);
        if (funcDef != null) {
            final ICPPASTConstructorChainInitializer member = getMemberInitializer(var, funcDef);
            return memberIsDefaulted(complexCtor, member);
        }
        return false;
    }

    private ICPPASTFunctionDefinition getFuncDef(final IASTNode parent) {
        if (parent instanceof ICPPASTFunctionDefinition) {
            return (ICPPASTFunctionDefinition) parent;
        } else if (parent instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) parent);
        }
        return null;
    }

    private ICPPASTConstructorChainInitializer getMemberInitializer(final IASTSimpleDeclaration var, final ICPPASTFunctionDefinition funcDef) {
        final IASTName varName = var.getDeclarators()[0].getName();
        for (final ICPPASTConstructorChainInitializer ctorInit : funcDef.getMemberInitializers()) {
            if (ctorInit.getChildren()[0].getRawSignature().contains(varName.getRawSignature())) {
                //TODO if (ASTHelper.isSameName((IASTName) ctorInit.getChildren()[0], varName)) {
                return ctorInit;
            }
        }
        return null;
    }

    private boolean memberIsDefaulted(final ICPPASTFunctionDeclarator complexCtor, final ICPPASTConstructorChainInitializer member) {
        final IASTName paramName = findParamName(member);
        return paramName != null ? checkIfDefaulted(complexCtor, paramName) : false;
    }

    private IASTName findParamName(final ICPPASTConstructorChainInitializer member) {
        if (member == null || member.getInitializer() == null) return null;
        for (final IASTNode node : member.getInitializer().getChildren()) {
            if (node instanceof IASTIdExpression) {
                return ((IASTIdExpression) node).getName();
            }
        }
        return null;
    }

    private boolean checkIfDefaulted(final ICPPASTFunctionDeclarator complexCtor, final IASTName paramName) {
        final ICPPASTParameterDeclaration params[] = complexCtor.getParameters();
        for (final ICPPASTParameterDeclaration param : params) {
            final ICPPASTDeclarator decl = param.getDeclarator();
            if (decl.getName().getRawSignature().contains(paramName.getRawSignature())) {
                //TODO if (ASTHelper.isSameName(decl.getName(), paramName)) {
                return decl.getInitializer() instanceof IASTEqualsInitializer;
            }
        }
        return false;
    }

    public ICPPASTFunctionDeclarator evalNewFuncDec(final ICPPASTFunctionDeclarator funcDec, final ICPPASTFunctionDeclarator otherCtor) {
        return otherCtor.getParameters().length > funcDec.getParameters().length ? otherCtor : funcDec;
    }

    public ICPPASTFunctionDeclarator getCtorWithMostParams(final ICPPASTCompositeTypeSpecifier struct) {
        ICPPASTFunctionDeclarator funcDec = null;
        for (final IASTNode node : struct.getChildren()) {
            if (isConstructor(node)) {
                if (funcDec == null) {
                    funcDec = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(node);
                } else {
                    final ICPPASTFunctionDeclarator otherCtor = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(node);
                    funcDec = evalNewFuncDec(funcDec, otherCtor);
                }
            }
        }
        return funcDec;
    }

    public boolean isConstructor(final IASTNode node) {
        if (node instanceof IASTSimpleDeclaration) {
            return doesSimpleDeclarationNameMatchWithTypeSpecifierName((IASTSimpleDeclaration) node);
        } else if (node instanceof ICPPASTFunctionDefinition) {
            return ASTHelper.doesFunctionDefinitionNameMatchWithTypeSpecifierName(node);
        }
        return false;
    }

    public boolean doesSimpleDeclarationNameMatchWithTypeSpecifierName(final IASTSimpleDeclaration simDec) {
        if (simDec.getDeclarators().length > 0) {
            return ASTHelper.getCompositeTypeSpecifier(simDec).getName().toString().contentEquals(simDec.getDeclarators()[0].getName().toString());
        }
        return false;
    }

}
