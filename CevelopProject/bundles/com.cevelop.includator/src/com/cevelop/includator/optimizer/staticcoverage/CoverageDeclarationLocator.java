package com.cevelop.includator.optimizer.staticcoverage;

import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceAlias;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;


public class CoverageDeclarationLocator extends ASTVisitor {

    private final Collection<IASTNode> declarationList;

    public CoverageDeclarationLocator(Collection<IASTNode> declarationList) {
        super(true);
        this.declarationList = declarationList;
    }

    @Override
    public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
        addChildren(namespaceDefinition.getDeclarations());
        return super.visit(namespaceDefinition);
    }

    @Override
    public int visit(IASTTranslationUnit tu) {
        addChildren(tu.getChildren());
        return super.visit(tu);
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof IASTCompositeTypeSpecifier) {
            addChildren(((IASTCompositeTypeSpecifier) declSpec).getDeclarations(false));
            return super.visit(declSpec);
        }
        return PROCESS_SKIP;
    }

    private void addChildren(IASTNode[] iastNodes) {
        for (IASTNode curNode : iastNodes) {
            if (shouldConsider(curNode)) {
                declarationList.add(curNode);
            }
        }
    }

    private boolean shouldConsider(IASTNode curNode) {
      //@formatter:off
		return !(curNode instanceof ICPPASTNamespaceDefinition) &&
			!(curNode instanceof ICPPASTVisibilityLabel) &&
			!(curNode instanceof ICPPASTUsingDirective) &&
			!(curNode instanceof ICPPASTNamespaceAlias) &&
			!isFriendDeclaration(curNode);
		//@formatter:on
    }

    private boolean isFriendDeclaration(IASTNode curNode) {
        if (curNode instanceof IASTSimpleDeclaration) {
            IASTDeclSpecifier declSpecifier = ((IASTSimpleDeclaration) curNode).getDeclSpecifier();
            if (declSpecifier instanceof ICPPASTDeclSpecifier) {
                return ((ICPPASTDeclSpecifier) declSpecifier).isFriend();
            }
        }
        return false;
    }

    @Override
    public int visit(ICPPASTBaseSpecifier baseSpecifier) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(ICPPASTTemplateParameter templateParameter) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTName name) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (declaration instanceof IASTFunctionDefinition) {
            return PROCESS_SKIP;
        }
        return super.visit(declaration);
    }

    @Override
    public int visit(IASTInitializer initializer) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTParameterDeclaration parameterDeclaration) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTArrayModifier arrayModifier) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTPointerOperator ptrOperator) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTExpression expression) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTStatement statement) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTTypeId typeId) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTEnumerator enumerator) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTProblem problem) {
        return PROCESS_SKIP;
    }
}
