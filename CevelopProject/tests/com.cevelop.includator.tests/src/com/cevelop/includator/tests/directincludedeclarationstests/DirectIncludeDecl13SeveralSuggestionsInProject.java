/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.directincludedeclarationstests;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.directlyincludereferenceddeclaration.DirectlyIncludeReferencedDeclarationsAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DirectIncludeDecl13SeveralSuggestionsInProject extends IncludatorTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(wrapAsProjectAlgorithm(new DirectlyIncludeReferencedDeclarationsAlgorithm()));

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "X.cpp", "Missing '#include \"B.h\"'.", 14, 1);
        assertSuggestion(suggestions.get(1), "main.cpp", "Missing '#include \"B.h\"'.", 14, 1);
    }
}
