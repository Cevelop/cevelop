package com.cevelop.intwidthfixator.tests.refactoring.conversion;

import org.eclipse.ltk.core.refactoring.Refactoring;

import com.cevelop.intwidthfixator.refactorings.conversion.ConversionRefactoring;


public class ConversionRefactoringLongTest extends AbstractConversionRefactoringTest {

    @Override
    protected Refactoring createRefactoring() {

        return new ConversionRefactoring(getPrimaryCElementFromCurrentProject().get(), info.also(c -> {
            c.refactor_char = false;
            c.refactor_short = false;
            c.refactor_int = false;
            c.refactor_long = true;
            c.refactor_longlong = false;
        }));
    }
}
