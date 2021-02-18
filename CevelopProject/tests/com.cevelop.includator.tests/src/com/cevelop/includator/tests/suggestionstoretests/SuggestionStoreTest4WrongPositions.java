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

import org.eclipse.core.resources.IFile;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.stores.SuggestionStore;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class SuggestionStoreTest4WrongPositions extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);
        SuggestionStore store = IncludatorPlugin.getSuggestionStore();
        String fileLocation = file.getLocation().toString();
        assertNoSuggestions(15, fileLocation, store);
        assertNoSuggestions(44, fileLocation, store);
        assertNoSuggestions(60, fileLocation, store);
        assertNoSuggestions(70, fileLocation, store);

    }

    private void assertNoSuggestions(int pos, String fileLocation, SuggestionStore store) {
        List<Suggestion<?>> suggestions = store.findSuggestions(fileLocation, pos);
        Assert.assertEquals((Object) 0, (Object) suggestions.size());
    }
}
