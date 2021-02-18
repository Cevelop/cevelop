package com.cevelop.gslator.tests.tests.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.cdt.internal.core.model.CProject;
import org.eclipse.ui.IMarkerResolution;
import org.junit.After;
import org.junit.Before;

import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;
import com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules.ES46_01UseNarrowQuickFix;


@SuppressWarnings("restriction")
public class ES46_01_01AvoidLossyFloatingPointToIntegerConversionsQuickFixTest extends ES46_00AvoidLossyConversionsQuickFixTest {

    String oldspacebeforesetting = "";
    String oldspaceaftersetting  = "";

    @Override
    @Before
    public void setUp() throws Exception { // TODO: remove once JUnit/CDT Bug fixed
        super.setUp();
        if (getCurrentCProject() instanceof CProject) { // Bug during JUnit Tests: gsl::narrow_cast<myint>(f); "<" and ">" get recognised as binary operator
            oldspacebeforesetting = getCurrentCProject().getOption("org.eclipse.cdt.core.formatter.insert_space_before_binary_operator", true);
            oldspaceaftersetting = getCurrentCProject().getOption("org.eclipse.cdt.core.formatter.insert_space_after_binary_operator", true);
            getCurrentCProject().setOption("org.eclipse.cdt.core.formatter.insert_space_before_binary_operator", "do not insert");
            getCurrentCProject().setOption("org.eclipse.cdt.core.formatter.insert_space_after_binary_operator", "do not insert");
        }
    }

    @After
    @Override
    public void tearDown() throws Exception {
        if (getCurrentCProject() instanceof CProject) {
            getCurrentCProject().setOption("org.eclipse.cdt.core.formatter.insert_space_before_binary_operator", oldspacebeforesetting);
            getCurrentCProject().setOption("org.eclipse.cdt.core.formatter.insert_space_after_binary_operator", oldspaceaftersetting);
        }
        super.tearDown();
    }

    @Override
    protected IMarkerResolution getQuickFix() {
        return new ES46_01UseNarrowQuickFix();
    }

    @Override
    protected ES46LossyType getLossyType() {
        return ES46LossyType.FpToInt;
    }

}
