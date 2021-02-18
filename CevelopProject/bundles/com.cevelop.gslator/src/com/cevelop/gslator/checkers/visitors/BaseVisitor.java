package com.cevelop.gslator.checkers.visitors;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.util.AttributeMatcher;
import com.cevelop.gslator.checkers.visitors.util.ICheckIgnoreAttribute;
import com.cevelop.gslator.utils.ASTHelper;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


public abstract class BaseVisitor extends ASTVisitor implements ICheckIgnoreAttribute {

    protected final BaseChecker checker;

    protected BaseVisitor(final BaseChecker checker) {
        this.checker = checker;
        setShouldVisit();
    }

    @Override
    public String getIgnoreString() {
        return checker.getIgnoreString();
    }

    @Override
    public String getProfileGroup() {
        return checker.getProfileGroup();
    }

    @Override
    public String getNrInProfileGroup() {
        return checker.getNrInProfileGroup();
    }

    protected boolean hasNoNoexcept(final ICPPASTFunctionDeclarator funcDecl) {
        final ICPPASTExpression noexceptExpression = funcDecl.getNoexceptExpression();

        if (noexceptExpression instanceof ICPPASTLiteralExpression) {
            return ((ICPPASTLiteralExpression) noexceptExpression).getRawSignature().equals("false");
        }

        return noexceptExpression == null;
    }

    protected boolean nodeHasNoIgnoreAttribute(final ICheckIgnoreAttribute ignore, final IASTNode node) {
        return ignore == null || node == null || AttributeMatcher.check(ignore, node);
    }

    public ICPPASTFunctionDefinition getImplFromDeclaration(final IASTSimpleDeclaration decl) {
        final ASTHelper.SpecialFunction type = ASTHelper.getSpecialMemberFunctionType(decl);
        IASTName name = ASTHelper.getNameOfFunctionFromDeclaration(decl);//ASTHelper.getCompositeTypeSpecifier(decl).getName();
        if (name != null) {
            //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
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

    protected abstract void setShouldVisit();

    public boolean isConstructorDefinition(IASTNode node) {
        node = getParentIfNotDeclaration(node);
        if (node instanceof ICPPASTFunctionDefinition) {
            return ASTHelper.doesFunctionDefinitionNameMatchWithTypeSpecifierName(node);
        }
        return false;
    }

    private IASTNode getParentIfNotDeclaration(final IASTNode node) {
        if (!(node instanceof IASTDeclaration)) {
            return node.getParent();
        }
        return node;
    }

    public boolean isConstructorDeclaration(IASTNode node) {
        node = getParentIfNotDeclaration(node);
        if (node instanceof IASTSimpleDeclaration) {
            return ASTHelper.doesFunctionDefinitionNameMatchWithTypeSpecifierName(node);
        }
        return false;
    }

    protected boolean hasOnlyConstInitVal(final IASTDeclaration ctor) {
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

    protected boolean hasNoBody(final ICPPASTFunctionDefinition ctor) {
        final IASTNode childs[] = ctor.getChildren();
        final IASTNode node = childs[childs.length - 1];
        if (node instanceof ICPPASTCompoundStatement) {
            final ICPPASTCompoundStatement compStat = (ICPPASTCompoundStatement) node;
            final IASTNode stmts[] = compStat.getStatements();
            return stmts.length == 0;
        }
        return false;
    }

}
