package com.cevelop.clonewar.transformation.configuration.action;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;


/**
 * Action that can be applied to the transform configuration, to change names,
 * ordering, etc.
 *
 * @author ythrier(at)hsr.ch
 */
public interface IConfigChangeAction {

    /**
     * Apply the change to the configuration.
     *
     * @param config
     * Config to change.
     * @param status
     * Status.
     */
    public void applyChange(TransformConfiguration config, RefactoringStatus status);
}
