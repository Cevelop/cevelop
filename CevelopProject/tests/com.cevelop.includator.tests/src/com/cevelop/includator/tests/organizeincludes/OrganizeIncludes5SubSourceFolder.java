/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.organizeincludes;

import java.util.List;

import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class OrganizeIncludes5SubSourceFolder extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new OrganizeIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        assertStatusOk(status);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        String mainPath = makeOSPath("src/main.cpp");
        String missingMessage = "Missing '#include \"../header/A.h\"'.";
        String addMessage = "Add '#include \"../header/A.h\"'.";
        assertSuggestion(suggestions.get(0), mainPath, missingMessage, 0, 1);
        assertQuickFix(addMessage, 0, 1, suggestions.get(0));
    }
}
