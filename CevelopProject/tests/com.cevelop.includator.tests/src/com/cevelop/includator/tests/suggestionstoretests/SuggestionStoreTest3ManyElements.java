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


public class SuggestionStoreTest3ManyElements extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);
        SuggestionStore store = IncludatorPlugin.getSuggestionStore();
        String fileLocation = getActiveIncludatorFile().getFilePath();

        List<Suggestion<?>> suggestions = store.findSuggestions(fileLocation, 3);
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Suggestion<?> suggestion = suggestions.get(0);
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", suggestion.getDescription());

        suggestions = store.findSuggestions(getActiveIncludatorFile().getProjectRelativePath(), adaptExpectedOffset(getActiveIncludatorFile()
                .getIFile(), 47));
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        suggestion = suggestions.get(0);
        Assert.assertEquals("The include statement '#include \"E.h\"' is unneeded. No reference requires include.", suggestion.getDescription());
    }
}
