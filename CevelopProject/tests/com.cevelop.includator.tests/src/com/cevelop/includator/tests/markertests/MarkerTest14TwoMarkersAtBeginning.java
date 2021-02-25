/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests;

import java.util.List;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class MarkerTest14TwoMarkersAtBeginning extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 2, (Object) suggestions.size());

        Assert.assertEquals((Object) 0, (Object) suggestions.get(0).getQuickFixes()[0].getStartOffset());
        Assert.assertEquals((Object) 0, (Object) suggestions.get(1).getQuickFixes()[0].getStartOffset());
        Suggestion<?> sug0 = suggestions.get(0);
        Suggestion<?> sug1 = suggestions.get(1);
        applySuggestion(sug0);

        Change change = sug1.getQuickFixes()[0].getChange(getActiveIncludatorFile().getIFile());
        assert (change instanceof TextFileChange);
        TextFileChange tfc = (TextFileChange) change;
        TextEdit edit = tfc.getEdit();
        assert (edit instanceof InsertEdit);
        InsertEdit insertEdit = (InsertEdit) edit;
        Assert.assertEquals("#include \"D.h\"" + FileHelper.NL, insertEdit.getText());
    }
}
