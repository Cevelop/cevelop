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
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class SuggestionStoreTest5QuickFixPositions extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);

        String fileLocation = getActiveIncludatorFile().getFilePath();
        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().findSuggestions(fileLocation, 3);

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Suggestion<?> suggestion = suggestions.get(0);
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", suggestion.getDescription());

        int nodeStartOffset = suggestion.getStartOffset();
        int nodeEndOffset = suggestion.getEndOffset();
        int quickFixStartOffset = suggestion.getQuickFixes()[0].getStartOffset();
        int quickFixEndOffset = suggestion.getQuickFixes()[0].getEndOffset();
        int expectedStartOffset = 0;
        int expcetedEndOffset = 14;
        Assert.assertEquals((Object) expectedStartOffset, (Object) nodeStartOffset);
        Assert.assertEquals((Object) expcetedEndOffset, (Object) nodeEndOffset);
        Assert.assertEquals((Object) expectedStartOffset, (Object) quickFixStartOffset);
        Assert.assertEquals((Object) expcetedEndOffset, (Object) quickFixEndOffset);
    }
}
