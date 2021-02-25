/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.testframeworktest;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.tests.base.IncludatorTest;


public class CheckNoUnresolvedInclusionsTest extends IncludatorTest implements ILogListener {

    IStatus loggedStatus;
    String  loggingPlugin;

    @Override
    public void setUp() throws Exception {
        Plugin plugin = CCorePlugin.getDefault();
        if (plugin != null) {
            plugin.getLog().addLogListener(this);
        }
        super.setUp();
    }

    @Test
    public void runTest() throws Throwable {
        Assert.assertNotNull(loggedStatus);
        Assert.assertNull(loggedStatus.getException());
        Assert.assertEquals(CCorePlugin.PLUGIN_ID, loggingPlugin);
        Assert.assertEquals((Object) IStatus.INFO, (Object) loggedStatus.getSeverity());
        Assert.assertTrue(loggedStatus.getMessage().startsWith("Indexed '" + getExpectedProject().getName() + "' (1 sources, 0 headers)"));
    }

    @Override
    public void logging(IStatus status, String plugin) {
        loggedStatus = status;
        loggingPlugin = plugin;
    }
}
