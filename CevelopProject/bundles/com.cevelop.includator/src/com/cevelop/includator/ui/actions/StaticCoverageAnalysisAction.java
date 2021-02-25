/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.actions;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.staticcoverage.StaticCoverageProjectAnalysisAlgorithm;
import com.cevelop.includator.resources.IncludatorProject;


public class StaticCoverageAnalysisAction extends IncludatorAlgorithmAction {

    @Override
    protected Algorithm getAlgorithmToRun() {
        return new StaticCoverageProjectAnalysisAlgorithm();
    }

    @Override
    protected boolean shouldShowDialog(IncludatorProject project) {
        return false;
    }
}
