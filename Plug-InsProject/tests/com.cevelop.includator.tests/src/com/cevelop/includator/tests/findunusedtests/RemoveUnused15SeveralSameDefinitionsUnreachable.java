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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class RemoveUnused15SeveralSameDefinitionsUnreachable extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new FindUnusedIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        String expectedStatusStr =
                                 "Status WARNING: com.cevelop.includator code=0 Includator Static Include Analysis Status null children=[Status WARNING: com.cevelop.includator code=0 Could not find any path from file \"A.cpp\" to \"B2.h\" (when following #includes). null]";
        assertStatus(1, IStatus.WARNING, expectedStatusStr, status);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "A.cpp", "The include statement '#include \"B.h\"' is unneeded. No reference requires include.", 0, 14);
        assertSuggestion(suggestions.get(1), "A.cpp", "The include statement '#include \"B3.h\"' is unneeded. No reference requires include.", 15,
                15);
    }
}
