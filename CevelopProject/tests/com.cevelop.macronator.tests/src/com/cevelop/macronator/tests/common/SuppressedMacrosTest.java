package com.cevelop.macronator.tests.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;

import com.cevelop.macronator.MacronatorPlugin;
import com.cevelop.macronator.common.SuppressedMacros;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;


public class SuppressedMacrosTest extends CDTTestingUITest {

    private ProjectScope projectScope;

    @Test
    public void runTest() throws Throwable {
        projectScope = new ProjectScope(getCurrentProject());
        IEclipsePreferences node = projectScope.getNode(MacronatorPlugin.PLUGIN_ID);
        node.put(MacronatorPlugin.SUPPRESSED_MACROS_PREF_KEY, "SUPPRESSED_MACRO_1;SUPPRESSED_MACRO_2");
        SuppressedMacros suppressedMacros = new SuppressedMacros(getCurrentProject());
        assertFalse(suppressedMacros.isSuppressed("UNSUPPRESSED_MACRO"));
        assertTrue(suppressedMacros.isSuppressed("SUPPRESSED_MACRO_1"));
        assertTrue(suppressedMacros.isSuppressed("SUPPRESSED_MACRO_2"));
        suppressedMacros.remove("SUPPRESSED_MACRO_1");
        assertFalse(suppressedMacros.isSuppressed("SUPPRESSED_MACRO_1"));
        suppressedMacros.add("MACRO");
        assertTrue(suppressedMacros.isSuppressed("MACRO"));
    }
}
