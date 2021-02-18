package com.cevelop.elevator.quickfix;

import org.eclipse.cdt.codan.core.cxx.Activator;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.elevator.ast.analysis.NodeProperties;
import com.cevelop.elevator.ast.transformation.ConstructorChainConverter;
import com.cevelop.elevator.ast.transformation.DeclaratorConverter;
import com.cevelop.elevator.ast.transformation.NewExpressionConverter;


/**
 * Elevates declarations, new-expressions, and constructor chains to the new
 * Uniform Initializer syntax.
 */
public class InitializationQuickFix extends ElevatorQuickFix {

    private static final int AST_STYLE = ITranslationUnit.AST_SKIP_INDEXED_HEADERS | ITranslationUnit.AST_PARSE_INACTIVE_CODE;

    private IASTTranslationUnit ast;
    private NodeProperties      astNodeProperties;
    private IMarker             marker;

    @Override
    public String getLabel() {
        return "Replace with uniform variable initialization";
    }

    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {
        try {
            this.marker = marker;
            this.ast = getTranslationUnitViaWorkspace(marker).getAST(index, AST_STYLE);
            this.astNodeProperties = new NodeProperties(getAstNameFromMarker(marker));
            if (astNodeProperties.hasAncestor(ICPPASTNewExpression.class)) {
                transformNewExpression();
            } else if (astNodeProperties.hasAncestor(IASTDeclarator.class)) {
                transformDeclarator();
            } else if (astNodeProperties.hasAncestor(ICPPASTConstructorChainInitializer.class)) {
                transformConstructorchainInitializer();
            }
        } catch (CoreException e) {
            Activator.log(e);
        }
    }

    private void transformConstructorchainInitializer() throws CoreException {
        ICPPASTConstructorChainInitializer initializer = astNodeProperties.getAncestor(ICPPASTConstructorChainInitializer.class);
        ICPPASTConstructorChainInitializer convertedInitializer = new ConstructorChainConverter(initializer).convert();
        performChange(initializer, convertedInitializer, ast);
        marker.delete();
    }

    private void transformDeclarator() throws CoreException {
        IASTDeclarator declarator = astNodeProperties.getAncestor(IASTDeclarator.class);
        IASTDeclarator convertedDeclarator = new DeclaratorConverter(declarator).convert();
        performChange(declarator, convertedDeclarator, ast);
    }

    private void transformNewExpression() throws CoreException {
        ICPPASTNewExpression expression = astNodeProperties.getAncestor(ICPPASTNewExpression.class);
        ICPPASTNewExpression convertedExpression = new NewExpressionConverter(expression).convert();
        performChange(expression, convertedExpression, ast);
    }

    private IASTNode getAstNameFromMarker(IMarker marker) {
        int markerOffset = marker.getAttribute(IMarker.CHAR_START, -1);
        int markerLength = marker.getAttribute(IMarker.CHAR_END, -1) - markerOffset;
        return ast.getNodeSelector(null).findEnclosingNode(markerOffset, markerLength);
    }
}
