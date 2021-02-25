/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorPositionTrackingChangeTest;
import com.cevelop.includator.ui.actions.ReplaceIncludesWithFwdAction;
import com.cevelop.includator.ui.helpers.PositionTrackInfo;
import com.cevelop.includator.ui.helpers.PositionTrackingChange;


public class MarkerTest13PositionChangeTestUserTypingDiscarded extends IncludatorPositionTrackingChangeTest {

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();

        runAction(new ReplaceIncludesWithFwdAction(), AlgorithmScope.EDITOR_SCOPE);
        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        PositionTrackingChange change = suggestions.get(0).getContainedPositionTrackingChange();
        ArrayList<PositionTrackInfo> positions = getPositions(change);
        Assert.assertEquals((Object) 2, (Object) positions.size());

        int expectedOffset = adaptExpectedOffset(currentProjectHolder.makeProjectAbsoluteURI(getNameOfPrimaryTestFile()), 30);
        PositionTrackInfo positionInfoToTest = positions.get(1);
        assertPositionInfo(positionInfoToTest, expectedOffset);

        String insertText = "//hello" + FileHelper.NL;
        insertTextIntoActiveDocument(insertText, 0);
        int newExpectedOffset = expectedOffset + insertText.length();
        assertPositionInfo(positionInfoToTest, newExpectedOffset, expectedOffset);

        closeOpenEditors(); // discarding "user typing"
        assertPositionInfo(positionInfoToTest, expectedOffset);

        openPrimaryTestFileInEditor();
        assertPositionInfo(positionInfoToTest, expectedOffset);
    }
}
