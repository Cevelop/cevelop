/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.actions;

import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.preferences.IncludatorPropertyManager;


public class OrganizeIncludesAction extends IncludatorAlgorithmAction {

    @Override
    protected Algorithm getAlgorithmToRun() {
        return new OrganizeIncludesAlgorithm();
    }

    @Override
    protected boolean shouldSuggestToAdaptIndexBeforeRun() {
        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        return store.getBoolean(IncludatorPropertyManager.ASK_TO_ADAPT_INDEX);
    }
}
