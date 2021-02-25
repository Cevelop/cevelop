package com.cevelop.includator.tests.base;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.text.edits.TextEdit;
import org.junit.Assert;

import com.cevelop.includator.ui.helpers.PositionTrackInfo;
import com.cevelop.includator.ui.helpers.PositionTrackingChange;


public abstract class IncludatorPositionTrackingChangeTest extends IncludatorTest {

    protected ArrayList<PositionTrackInfo> getPositions(PositionTrackingChange change) {
        Map<TextEdit, PositionTrackInfo> positionMap = change.getPositionMap();
        return new ArrayList<>(positionMap.values());
    }

    protected void assertPositionInfo(PositionTrackInfo positionInfoToTest, int expectedOffset) {
        assertPositionInfo(positionInfoToTest, expectedOffset, expectedOffset);
    }

    protected void assertPositionInfo(PositionTrackInfo positionInfoToTest, int expectedCurrentOffset, int expectedOriginalOffset) {
        Assert.assertEquals((Object) expectedCurrentOffset, (Object) positionInfoToTest.getCurrentPosition().getOffset());
        Assert.assertEquals((Object) expectedOriginalOffset, (Object) positionInfoToTest.getOriginalPosition().getOffset());
    }

}
