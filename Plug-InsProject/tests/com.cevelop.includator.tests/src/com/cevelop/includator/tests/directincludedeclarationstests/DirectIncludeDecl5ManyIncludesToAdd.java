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
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.directlyincludereferenceddeclaration.DirectlyIncludeReferencedDeclarationsAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DirectIncludeDecl5ManyIncludesToAdd extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new DirectlyIncludeReferencedDeclarationsAlgorithm());

        Assert.assertEquals((Object) 3, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "A.cpp", "Missing '#include \"C.h\"'.", 29, 1);
        assertSuggestion(suggestions.get(1), "A.cpp", "Missing '#include \"D.h\"'.", 29, 1);
        assertSuggestion(suggestions.get(2), "A.cpp", "Missing '#include \"F.h\"'.", 29, 1);
    }
}
