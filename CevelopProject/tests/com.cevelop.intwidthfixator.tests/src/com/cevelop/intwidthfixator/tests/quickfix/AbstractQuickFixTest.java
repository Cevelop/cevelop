package com.cevelop.intwidthfixator.tests.quickfix;

import java.util.EnumSet;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.preferences.PropAndPrefHelper;
import com.cevelop.intwidthfixator.quickfixes.IntQuickFix;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTestWithPreferences;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;


public abstract class AbstractQuickFixTest extends CDTTestingQuickfixTestWithPreferences {

    @Override
    public IPreferenceStore initPrefs() {
        return PropAndPrefHelper.getInstance().getWorkspacePreferences();
    }

    @Override
    public Class<?> getPreferenceConstants() {
        return IdHelper.class;
    }

    @Test
    public void runTest() throws Throwable {
        runQuickfixForAllMarkersAndAssertAllEqual();
    }

    @Override
    protected EnumSet<ComparisonArg> makeComparisonArguments() {
        return EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES, ComparisonArg.PRINT_WHOLE_ASTS_ON_FAIL);
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new IntQuickFix();
    }

}
