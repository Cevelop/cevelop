package com.cevelop.clonewar.transformation.configuration.action;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;


/**
 * Action to disable all other actions except the action which corresponds to
 * the single selection of the refactoring.
 *
 * @author ythrier(at)hsr.ch
 */
public class SingleSelectionChangeAction implements IConfigChangeAction {

    private IASTNode originalParentNode_;
    private IASTNode copyParentNode_;
    private IASTNode originalSubNode_;

    /**
     * Create the single selection change action.
     *
     * @param originalParent
     * Original (frozen) parent node.
     * @param copyParent
     * Copy (not frozen) parent node.
     * @param originalSubNode
     * Original single selection node.
     */
    public SingleSelectionChangeAction(IASTNode originalParent, IASTNode copyParent, IASTNode originalSubNode) {
        this.originalParentNode_ = originalParent;
        this.copyParentNode_ = copyParent;
        this.originalSubNode_ = originalSubNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyChange(TransformConfiguration config, RefactoringStatus status) {
        if (!traverseRecursive(originalParentNode_, copyParentNode_, config)) {
            status.addError("Single selection was not found in the AST!");
        }
    }

    /**
     * Traverse the copy and the original AST tree to find the sub selection and
     * disabling all other actions.
     *
     * @param origParent
     * Parent node (original AST).
     * @param copyParent
     * Copy node (copy AST).
     * @param config
     * Configuration.
     * @return True if the single selection was found, otherwise false.
     */
    private boolean traverseRecursive(IASTNode origParent, IASTNode copyParent, TransformConfiguration config) {
        if (origParent == originalSubNode_) {
            disableActionExceptNodeMatch(copyParent, config);
            return true;
        }
        for (int i = 0; i < origParent.getChildren().length; ++i) {
            if (traverseRecursive(origParent.getChildren()[i], copyParent.getChildren()[i], config)) return true;
        }
        return false;
    }

    /**
     * Disable all actions except the action with the node match.
     *
     * @param node
     * Node.
     * @param config
     * Config.
     */
    private void disableActionExceptNodeMatch(IASTNode node, TransformConfiguration config) {
        for (TransformAction action : config.getAllActions()) {
            if (!(action.getNode() == node)) action.setPerform(false);
        }
    }
}
