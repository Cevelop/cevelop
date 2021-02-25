package com.cevelop.gslator.checkers.visitors.C20ToC22DefaultOperationsRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C20RedundantOperationsVisitor extends BaseVisitor {

    public C20RedundantOperationsVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {

            final List<IASTSimpleDeclaration> memberVars = ASTHelper.collectMemberVariables((ICPPASTCompositeTypeSpecifier) declSpec);

            if (hasGslOwner(memberVars)) {
                return PROCESS_CONTINUE;
            }

            for (final IASTDeclaration decl : ((ICPPASTCompositeTypeSpecifier) declSpec).getMembers()) {
                final ICPPASTFunctionDefinition function = getFunctionDefinition(decl);

                if (function != null && canBeReported(memberVars, decl, function)) {
                    checker.reportProblem(ProblemId.P_C20, getDeclarator(decl));
                }
            }
        }
        return super.visit(declSpec);
    }

    private IASTDeclarator getDeclarator(final IASTDeclaration decl) {
        if (decl instanceof IASTSimpleDeclaration) {
            final IASTSimpleDeclaration simDec = (IASTSimpleDeclaration) decl;
            return simDec.getDeclarators()[0];
        } else if (decl instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition funcDef = (ICPPASTFunctionDefinition) decl;
            return funcDef.getDeclarator();
        }
        return null;
    }

    private ICPPASTFunctionDefinition getFunctionDefinition(final IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) declaration);
        }
        if (declaration instanceof ICPPASTFunctionDefinition) {
            return (ICPPASTFunctionDefinition) declaration;
        }
        return null;
    }

    private boolean canBeReported(final List<IASTSimpleDeclaration> memberVars, final IASTDeclaration decl,
            final ICPPASTFunctionDefinition function) {
        return nodeHasNoIgnoreAttribute(this, decl) && isDefaulOperation(function) && !isFunctionDefinitionNecessary(function, memberVars) &&
               !shouldNotBeHandledByC45(function);
    }

    private boolean isFunctionDefinitionNecessary(final ICPPASTFunctionDefinition funcDecl, final List<IASTSimpleDeclaration> memberVars) {
        return !hasEmptyBody(funcDecl) || hasGslOwner(memberVars) || hasInitializerList(funcDecl);
    }

    private boolean shouldNotBeHandledByC45(final ICPPASTFunctionDefinition function) {
        if (ASTHelper.isDefaultConstructor(function)) {
            return hasOnlyConstInitVal(function) && areNotDefaultInited(function);
        }
        return false;
    }

    private boolean areNotDefaultInited(final ICPPASTFunctionDefinition function) {
        final ICPPASTConstructorChainInitializer[] memInits = function.getMemberInitializers();
        for (final ICPPASTConstructorChainInitializer memInit : memInits) {
            final IASTInitializer initializer = memInit.getInitializer();
            if (!(initializer instanceof ICPPASTInitializerList && initializer.getChildren().length == 1 && isNotConstInited(initializer))) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotConstInited(final IASTInitializer initializer) {
        final IASTNode iastNode = initializer.getChildren()[0];
        return iastNode instanceof IASTLiteralExpression || isNullPtr(iastNode);
    }

    private boolean isNullPtr(final IASTNode node) {
        final String replaceAll = node.toString().replaceAll(" ", "");
        return replaceAll.equals("NULL") || replaceAll.equals("nullptr");
    }

    private boolean isDefaulOperation(final ICPPASTFunctionDefinition function) {
        return ASTHelper.isDefaultConstructor(function) || ASTHelper.isDefaultCopyConstructor(function) || ASTHelper.isDefaultCopyAssignment(
                function) || ASTHelper.isDefaultDestructor(function);
    }

    private boolean hasInitializerList(final ICPPASTFunctionDefinition funcDecl) {
        if (funcDecl.getMemberInitializers().length != 0) {
            ICPPASTConstructorChainInitializer[] initializers = funcDecl.getMemberInitializers();
            for (ICPPASTConstructorChainInitializer chainInitializer : initializers) {
                if (chainInitializer.getChildren().length >= 2) {
                    IASTNode initializer = chainInitializer.getChildren()[1];
                    if (initializer instanceof IASTInitializerList) {
                        IASTInitializerList list = (IASTInitializerList) initializer;
                        if (list.getChildren().length != 0) {
                            return true;
                        }
                    }
                    if (initializer instanceof ICPPASTConstructorInitializer) {
                        if (initializer.getChildren().length != 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasEmptyBody(final ICPPASTFunctionDefinition funcDecl) {
        if (funcDecl.getBody() == null) {
            return false;
        } else if (funcDecl.getBody() instanceof ICPPASTCompoundStatement) {
            return ((ICPPASTCompoundStatement) funcDecl.getBody()).getStatements().length == 0;
        }
        return false;
    }

    public boolean hasGslOwner(final List<IASTSimpleDeclaration> memberVars) {
        for (final IASTSimpleDeclaration decl : memberVars) {
            if (ASTHelper.isGslOwner(decl)) {
                return true;
            }
        }
        return false;
    }
}
