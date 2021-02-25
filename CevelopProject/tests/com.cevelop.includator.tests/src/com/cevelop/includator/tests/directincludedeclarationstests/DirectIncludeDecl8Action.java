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

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.DirectlyIncludeReferencedDeclarationsAction;


public class DirectIncludeDecl8Action extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new DirectlyIncludeReferencedDeclarationsAction(), AlgorithmScope.FILE_SCOPE);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "A.cpp", "Missing '#include \"C.h\"'.", 14, 1);
    }
}
