/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.tests.helpers.StatusHelper;

import junit.framework.TestCase;


public class HelperTest6FindHeaderFileIStatus1 extends TestCase {

    private static final String DUMMY_PATH = "dummy/path";
    private MultiStatus         multiStatus;
    private MultiStatus         multiStatus2;

    @Override
    @Before
    public void setUp() throws Exception {
        multiStatus = new MultiStatus(IncludatorPlugin.PLUGIN_ID, -1, "SomeMessage", null);
        multiStatus2 = new MultiStatus(IncludatorPlugin.PLUGIN_ID, -1, "SomeMessage", null);
    }

    @Test
    public void test1Status() {
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest"), DUMMY_PATH);
        multiStatus = StatusHelper.collectStatus();
        multiStatus2 = StatusHelper.collectStatus();
        assertEquals(1, multiStatus.getChildren().length);
        assertEquals(0, multiStatus2.getChildren().length);
    }

    @Test
    public void testSeveral() {
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest"), DUMMY_PATH);
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest2"), DUMMY_PATH);
        multiStatus = StatusHelper.collectStatus();
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest"), DUMMY_PATH);
        multiStatus2 = StatusHelper.collectStatus();
        assertEquals(2, multiStatus.getChildren().length);
        assertEquals(1, multiStatus2.getChildren().length);
    }

    @Test
    public void testRedundantMessage() {
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest"), DUMMY_PATH);
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest2"), DUMMY_PATH);
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest"), DUMMY_PATH);
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest3"), DUMMY_PATH);
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest"), DUMMY_PATH);
        IncludatorPlugin.logStatus(new Status(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, "testtest2"), DUMMY_PATH);
        multiStatus = StatusHelper.collectStatus();
        assertEquals(3, multiStatus.getChildren().length);
    }
}
