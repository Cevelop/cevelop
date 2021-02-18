package com.cevelop.gslator.quickfixes;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.gslator.utils.ASTHelper;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


@SuppressWarnings("restriction")
public abstract class RuleQuickFix extends BaseQuickFix {

    public RuleQuickFix() {
        super();
    }

    protected void insertNodesUnderVisibilityLabel(final ASTRewrite mainRewrite, final ICPPASTCompositeTypeSpecifier struct,
            final List<IASTNode> nodes, final int visibilityLabel) {
        final ICPPASTVisibilityLabel visibilityNode = findVisibilityLabel(struct, visibilityLabel);
        if (visibilityNode == null) {
            final int visibility = ASTHelper.getDefaultVisibilityForStruct(struct);
            final IASTNode insertionPoint = getInsertionPoint(mainRewrite, struct, visibilityLabel, visibility);
            insertNodes(mainRewrite, struct, nodes, insertionPoint);
        } else {
            insertNodes(mainRewrite, struct, nodes, visibilityNode);
        }
    }

    private void insertNodes(final ASTRewrite mainRewrite, final ICPPASTCompositeTypeSpecifier struct, final List<IASTNode> nodes,
            final ICPPASTVisibilityLabel visibilityNode) {
        for (final IASTNode node : nodes) {
            insertBefore(mainRewrite, struct, getNextNode(visibilityNode), node);
        }
    }

    private void insertNodes(final ASTRewrite mainRewrite, final ICPPASTCompositeTypeSpecifier struct, final List<IASTNode> nodes,
            final IASTNode insertionPoint) {
        for (final IASTNode node : nodes) {
            insertBefore(mainRewrite, struct, insertionPoint, node);
        }
    }

    private void insertBefore(final ASTRewrite mainRewrite, final ICPPASTCompositeTypeSpecifier struct, final IASTNode insertionPoint,
            final IASTNode newNode) {
        mainRewrite.insertBefore(struct, insertionPoint, newNode, null);
    }

    private IASTNode getInsertionPoint(final ASTRewrite mainRewrite, final ICPPASTCompositeTypeSpecifier struct, final int visibilityLabel,
            final int visibility) {
        if (visibility == visibilityLabel) {
            return struct.getDeclarations(false)[0];
        } else {
            final IASTNode insertionPoint = null;
            final ICPPASTVisibilityLabel label = factory.newVisibilityLabel(visibilityLabel);
            mainRewrite.insertBefore(struct, insertionPoint, label, null);
            return insertionPoint;
        }
    }

    public IASTNode getNextNode(final IASTNode node) {
        boolean readyToPull = false;
        for (final IASTNode nextNode : node.getParent().getChildren()) {
            if (readyToPull) {
                return nextNode;
            }
            if (nextNode == node) {
                readyToPull = true;
            }
        }
        return null;
    }

    private ICPPASTVisibilityLabel findVisibilityLabel(final ICPPASTCompositeTypeSpecifier struct, final int shouldVisibility) {
        for (final IASTNode child : struct.getChildren()) {
            if (child instanceof ICPPASTVisibilityLabel) {
                if (((ICPPASTVisibilityLabel) child).getVisibility() == shouldVisibility) {
                    return (ICPPASTVisibilityLabel) child;
                }
            }
        }
        return null;
    }

    public ICPPASTFunctionDefinition getImplFromDeclaration(final IASTSimpleDeclaration decl) {
        final ASTHelper.SpecialFunction type = ASTHelper.getSpecialMemberFunctionType(decl);
        final ICPPASTCompositeTypeSpecifier typeSpecifier = ASTHelper.getCompositeTypeSpecifier(decl);
        if (typeSpecifier == null) return null;
        final IASTName name = typeSpecifier.getName();

        //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
        final List<IASTNode> nodes = ASTHelper.findNames(astRewriteStore, name.resolveBinding(), CProjectUtil.getCProject(CProjectUtil.getProject(
                name)), IIndex.FIND_ALL_OCCURRENCES, null);

        for (final IASTNode node : nodes) {
            final ICPPASTFunctionDefinition funcDef = ASTHelper.getFunctionDefinition(node);
            if (funcDef != null && ASTHelper.getSpecialMemberFunctionType(funcDef) == type) {
                return funcDef;
            }
        }
        return null;
    }

    protected ICPPASTFunctionDefinition getFunctionDefinition(final IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) declaration).copy(CopyStyle.withLocations);
        }
        if (declaration instanceof ICPPASTFunctionDefinition) {
            return (ICPPASTFunctionDefinition) declaration.copy(CopyStyle.withLocations);
        }
        return null;
    }

    protected IASTDeclaration getSimpleDeclarationOrFunctionDefinition(IASTNode node) {
        while (!(node instanceof IASTSimpleDeclaration || node instanceof ICPPASTFunctionDefinition) && node != null) {
            node = node.getParent();
        }
        return (IASTDeclaration) node;
    }

}
