package com.cevelop.macronator.quickassist;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jface.text.Region;


public class SelectionResolver {

    private final int                 selectionOffset;
    private final int                 selectionLength;
    private final IASTTranslationUnit ast;

    public SelectionResolver(IASTTranslationUnit ast, int selectionOffset, int selectionLength) {
        this.ast = ast;
        this.selectionOffset = selectionOffset;
        this.selectionLength = selectionLength;
    }

    public SelectionResolver(IASTTranslationUnit ast, Region selectedRegion) {
        this(ast, selectedRegion.getOffset(), selectedRegion.getLength());
    }

    public IASTNode getSelectedNode() {
        return ast.getNodeSelector(null).findEnclosingNode(selectionOffset, selectionLength);
    }

    public IASTName getSelectedName() {
        IASTNode selectedNode = getSelectedNode();
        return ast.getNodeSelector(null).findFirstContainedName(selectedNode.getFileLocation().getNodeOffset(), selectedNode.getFileLocation()
                .getNodeLength());
    }
}
