/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorWorkspace;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public class Optimizer {

    private final Algorithm algorithm;

    public Optimizer(IncludatorWorkspace workspace, Algorithm alg) {
        algorithm = alg;
    }

    public List<Suggestion<?>> getOptimizationSuggestions() {
        return new ArrayList<>(algorithm.getSuggestions());
    }

    public void run(AlgorithmStartingPoint startingPoint, IProgressMonitor monitor) {
        String msg = "Running static analysis";
        monitor.setTaskName(msg);
        SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
        algorithm.setSuppressionList(IncludatorPropertyManager.getSuppressedSuggestions(startingPoint.getProject()));
        algorithm.start(startingPoint, subMonitor);
        SubMonitor.done(monitor);
    }
}
