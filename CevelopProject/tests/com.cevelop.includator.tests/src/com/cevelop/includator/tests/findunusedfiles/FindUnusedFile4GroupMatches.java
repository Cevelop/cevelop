/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedfiles;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedfiles.FindUnusedFilesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class FindUnusedFile4GroupMatches extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(wrapAsProjectAlgorithm(new FindUnusedFilesAlgorithm()));

        Assert.assertEquals((Object) 3, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "E.h", "The file 'E.h' is unneeded.", 0, 0);
        assertSuggestion(suggestions.get(1), "F.h", "The file 'F.h' is unneeded.", 0, 0);
        assertSuggestion(suggestions.get(2), "G.h", "The file 'G.h' is unneeded.", 0, 0);
    }
}
