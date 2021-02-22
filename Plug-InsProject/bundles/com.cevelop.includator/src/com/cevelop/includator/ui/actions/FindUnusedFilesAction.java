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
import com.cevelop.includator.optimizer.findunusedfiles.FindUnusedFilesAlgorithm;


public class FindUnusedFilesAction extends IncludatorAlgorithmAction {

    @Override
    protected Algorithm getAlgorithmToRun() {
        return new FindUnusedFilesAlgorithm();
    }
}
