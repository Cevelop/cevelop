package com.cevelop.codeanalysator.core.quickassist.rewrite;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;


/**
 * Interface for changes that can be applied to an AST, e.g. remove, replace, insert, etc.
 *
 * @author ythrier(at)hsr.ch
 */
public interface IASTChange {

    /**
     * Apply the change using the passed rewrite.
     *
     * @param rewrite
     * Rewrite to apply the change on.
     * @return New rewrite created by the change or null if no new rewrite was created.
     */
    public ASTRewrite apply(ASTRewrite rewrite);

    /**
     * Returns the rewrite root, this is the root used for the change.
     *
     * @return The rewrite root.
     */
    public IASTNode getRewriteRoot();

    /**
     * Return the change root, this is the root for the rewrite created by the change.
     *
     * @return The change root.
     */
    public IASTNode getChangeRoot();
}
