package com.cevelop.constificator.resolution;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;


public interface Resolution<T extends IASTNode> {

    public void handle(IASTNode node, IIndex index, ASTRewriteCache cache, T ancestor);

}
