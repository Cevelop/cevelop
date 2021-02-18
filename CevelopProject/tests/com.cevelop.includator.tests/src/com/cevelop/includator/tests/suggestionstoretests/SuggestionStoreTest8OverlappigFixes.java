/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suggestionstoretests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.stores.SuggestionStore;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class SuggestionStoreTest8OverlappigFixes extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);

        SuggestionStore store = IncludatorPlugin.getSuggestionStore();
        List<Suggestion<?>> suggestions = store.findSuggestions(getActiveIncludatorFile().getFilePath(), 14);
        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        Suggestion<?> missingInclude1 = suggestions.get(0);
        Assert.assertEquals("Missing '#include \"B.h\"'.", missingInclude1.getDescription());
        Assert.assertEquals("The include statement '#include \"C.h\"' is unneeded. No reference requires include.", suggestions.get(1)
                .getDescription());

        Suggestion<?> missingInclude2 = store.findSuggestion(missingInclude1.getMarker());
        Assert.assertEquals(missingInclude1, missingInclude2);
    }
}
