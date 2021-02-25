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


public class DirectIncludeDecl7QuickFix extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new DirectlyIncludeReferencedDeclarationsAlgorithm());

        Assert.assertEquals((Object) 1, (Object) suggestions.size());

        assertQuickFix("Add '#include \"F.h\"'.", 29, 30, suggestions.get(0));
    }
}
