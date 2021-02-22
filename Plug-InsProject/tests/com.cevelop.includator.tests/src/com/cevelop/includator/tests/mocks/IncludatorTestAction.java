/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.mocks;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.ui.actions.IncludatorAlgorithmAction;


public class IncludatorTestAction extends IncludatorAlgorithmAction {

    private final Algorithm algorithm;

    public IncludatorTestAction(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected Algorithm getAlgorithmToRun() {
        return algorithm;
    }
}
