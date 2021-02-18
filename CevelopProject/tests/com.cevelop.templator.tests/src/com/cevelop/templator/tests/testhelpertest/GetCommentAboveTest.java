package com.cevelop.templator.tests.testhelpertest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cevelop.templator.tests.TestHelper;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.ProjectHolderBaseTest;


public class GetCommentAboveTest extends ProjectHolderBaseTest {

    @Test
    // The Comment that i wrote
    // Is 2 Lines long
    public void testGetCommentAbove() throws Exception {

        String compareString = " The Comment that i wrote\n" + " Is 2 Lines long\n";

        String readComment = TestHelper.getCommentAbove(getClass());

        assertEquals(compareString, readComment);
    }

    @Override
    protected void initCurrentExpectedProjectHolders() throws Exception {}

}
