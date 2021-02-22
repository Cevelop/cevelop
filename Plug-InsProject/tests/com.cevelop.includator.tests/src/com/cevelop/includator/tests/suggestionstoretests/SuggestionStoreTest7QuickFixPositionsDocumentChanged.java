/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suggestionstoretests;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.stores.SuggestionStore;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class SuggestionStoreTest7QuickFixPositionsDocumentChanged extends IncludatorTest {

    private SuggestionStore store;
    private String          fileLocation;

    @Test
    public void runTest() throws Throwable {
        try {
            init();
            assertFindSuggestionAt(31);
            deleteDefine();
            assertFindSuggestionAt(2);
        } finally {
            closeOpenEditors();
        }
    }

    private void deleteDefine() throws Exception {
        deleteFromActiveDocument(0, 29);
    }

    private void assertFindSuggestionAt(int offset) throws IOException {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileLocation));
        List<Suggestion<?>> suggestions = store.findSuggestions(fileLocation, adaptExpectedOffset(file, offset));
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"C.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());
    }

    private void init() throws Exception {
        openPrimaryTestFileInEditor();
        runAction(new OrganizeIncludesAction(), AlgorithmScope.EDITOR_SCOPE);
        store = IncludatorPlugin.getSuggestionStore();
        fileLocation = getActiveIncludatorFile().getFilePath();
    }
}
