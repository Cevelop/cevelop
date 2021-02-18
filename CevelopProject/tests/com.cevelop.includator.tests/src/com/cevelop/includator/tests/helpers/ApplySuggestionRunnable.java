/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpers;

import com.cevelop.includator.optimizer.Suggestion;


public class ApplySuggestionRunnable implements Runnable {

    private final Suggestion<?> suggestion;

    public ApplySuggestionRunnable(Suggestion<?> suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public void run() {
        suggestion.getQuickFixes()[0].run(suggestion.getMarker()); // TODO: Index 0 might be wrong!
    }
}
