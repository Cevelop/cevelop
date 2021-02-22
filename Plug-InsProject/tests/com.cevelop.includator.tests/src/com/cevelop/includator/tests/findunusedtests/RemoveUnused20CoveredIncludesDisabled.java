/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.tests.base.IncludatorTest;


public class RemoveUnused20CoveredIncludesDisabled extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        store.setValue(IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME, false);

        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());
        Assert.assertEquals("The include statement '#include \"D.h\"' is unneeded. No reference requires include.", suggestions.get(1)
                .getDescription());

        store.setValue(IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME, true);
    }
}
