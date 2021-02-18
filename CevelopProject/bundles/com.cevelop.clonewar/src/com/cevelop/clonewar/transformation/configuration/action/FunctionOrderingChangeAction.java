package com.cevelop.clonewar.transformation.configuration.action;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;
import com.cevelop.clonewar.transformation.util.TypeInformation;


/**
 * Action to change the ordering of a function transformation. The action
 * proposes an ordering in which the return type is the first template
 * parameter.
 *
 * @author ythrier(at)hsr.ch
 */
public class FunctionOrderingChangeAction implements IConfigChangeAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyChange(TransformConfiguration config, RefactoringStatus status) {
        int i = 1;
        for (TypeInformation type : config.getAllTypes()) {
            if (config.hasReturnTypeAction(type))
                type.setOrderId(0);
            else type.setOrderId(i++);
        }
    }
}
