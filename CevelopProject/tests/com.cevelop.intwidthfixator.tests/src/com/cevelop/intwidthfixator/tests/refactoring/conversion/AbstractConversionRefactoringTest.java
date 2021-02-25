package com.cevelop.intwidthfixator.tests.refactoring.conversion;

import java.util.EnumSet;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

import com.cevelop.intwidthfixator.IntwidthfixatorPlugin;
import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.refactorings.conversion.ConversionInfo;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTestWithPreferences;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;


public abstract class AbstractConversionRefactoringTest extends CDTTestingRefactoringTestWithPreferences {

    protected ConversionInfo info = new ConversionInfo();

    @Test
    public void runTest() throws Throwable {
        info.also(c -> {
            c.fileName = getPrimaryCElementFromCurrentProject().get().getLocationURI().toString();
            c.setSelection(getSelectionOfPrimaryTestFile());
        });
        runRefactoringAndAssertSuccess();
    }

    @Override
    public IPreferenceStore initPrefs() {
        return IntwidthfixatorPlugin.getDefault().getPreferenceStore();
    }

    @Override
    protected EnumSet<ComparisonArg> makeComparisonArguments() {
        return EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES, ComparisonArg.PRINT_WHOLE_ASTS_ON_FAIL);
    }

    @Override
    public Class<?> getPreferenceConstants() {
        return IdHelper.class;
    }

}
