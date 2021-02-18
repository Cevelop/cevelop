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

import org.eclipse.ui.PartInitException;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class SuggestionStoreTest6QuickFixPositionsManyElements extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        try {
            runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);
            applyFirstQuickFix();
            Suggestion<?> secondSuggestion = getSuggestion();
            checkModifiedOffsets(secondSuggestion);
            applySecondQuickFix(secondSuggestion);
            assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);
        } finally {
            closeOpenEditors();
        }
    }

    private void applySecondQuickFix(final Suggestion<?> secondSuggestion) throws PartInitException, InterruptedException {
        Assert.assertEquals("The include statement '#include \"C.h\"' is unneeded. No reference requires include.", secondSuggestion
                .getDescription());
        applySuggestion(secondSuggestion);
    }

    private void checkModifiedOffsets(final Suggestion<?> suggestionToCheck) {
        int quickFixStartOffset = suggestionToCheck.getQuickFixes()[0].getStartOffset();
        int quickFixEndOffset = suggestionToCheck.getQuickFixes()[0].getEndOffset();
        Assert.assertEquals((Object) 0, (Object) quickFixStartOffset);
        Assert.assertEquals((Object) 14, (Object) quickFixEndOffset);
    }

    private Suggestion<?> getSuggestion() {
        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().findSuggestions(currentProjectHolder.makeProjectAbsolutePath(
                getNameOfPrimaryTestFile()).toOSString(), 3);
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        return suggestions.get(0);
    }

    private void applyFirstQuickFix() throws Exception {
        Suggestion<?> suggestionToRun = getSuggestion();
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", suggestionToRun.getDescription());
        applySuggestion(suggestionToRun);
    }
}
