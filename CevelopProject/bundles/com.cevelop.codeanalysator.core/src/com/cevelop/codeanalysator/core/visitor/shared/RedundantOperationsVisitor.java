package com.cevelop.codeanalysator.core.visitor.shared;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.index.IIndex;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class RedundantOperationsVisitor extends CodeAnalysatorVisitor {

    public RedundantOperationsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    /* BEGIN GSLATOR */
    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (!isHighestPriorityRuleForNode(declSpec)) {
            return PROCESS_CONTINUE;
        }
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {

            final List<IASTSimpleDeclaration> memberVars = ASTHelper.collectMemberVariables((ICPPASTCompositeTypeSpecifier) declSpec);

            if (hasGslOwner(memberVars)) {
                return PROCESS_CONTINUE;
            }

            for (final IASTDeclaration decl : ((ICPPASTCompositeTypeSpecifier) declSpec).getMembers()) {
                final ICPPASTFunctionDefinition function = getFunctionDefinition(decl);

                if (function != null && canBeReported(memberVars, decl, function)) {
                    reportRuleForNode(getDeclarator(decl));
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

    private ICPPASTFunctionDefinition getImplFromDeclaration(final IASTSimpleDeclaration decl) {
        final ASTHelper.SpecialFunction type = ASTHelper.getSpecialMemberFunctionType(decl);
        IASTName name = ASTHelper.getNameOfFunctionFromDeclaration(decl);//ASTHelper.getCompositeTypeSpecifier(decl).getName();
        if (name != null) {
            final List<IASTNode> nodes = ASTHelper.findNames(name.resolveBinding(), CProjectUtil.getCProject(CProjectUtil.getProject(decl)),
                    IIndex.FIND_DEFINITIONS);
            for (final IASTNode node : nodes) {
                final ICPPASTFunctionDefinition funcDef = ASTHelper.getFunctionDefinition(node);
                if (funcDef != null && ASTHelper.getSpecialMemberFunctionType(funcDef) == type) {
                    return funcDef;
                }
            }
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
        return isDefaulOperation(function) && !isFunctionDefinitionNecessary(function, memberVars) && !shouldNotBeHandledByC45(function);
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

    private boolean hasOnlyConstInitVal(final IASTDeclaration ctor) {
        if (ctor instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition ctorFuncDef = (ICPPASTFunctionDefinition) ctor;
            if (hasNoBody(ctorFuncDef)) {
                final ICPPASTConstructorChainInitializer[] inits = ctorFuncDef.getMemberInitializers();
                if (inits.length == 0) {
                    return false;
                }
                for (final ICPPASTConstructorChainInitializer init : inits) {
                    final IASTInitializer initializer = init.getInitializer();
                    if (initializer != null) {
                        for (final IASTNode expr : initializer.getChildren()) {
                            if (!(expr instanceof IASTExpression)) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean hasNoBody(final ICPPASTFunctionDefinition ctor) {
        final IASTNode childs[] = ctor.getChildren();
        final IASTNode node = childs[childs.length - 1];
        if (node instanceof ICPPASTCompoundStatement) {
            final ICPPASTCompoundStatement compStat = (ICPPASTCompoundStatement) node;
            final IASTNode stmts[] = compStat.getStatements();
            return stmts.length == 0;
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
