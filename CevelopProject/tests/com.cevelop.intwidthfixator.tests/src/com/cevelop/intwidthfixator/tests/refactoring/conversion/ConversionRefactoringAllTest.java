package com.cevelop.intwidthfixator.tests.refactoring.conversion;

import org.eclipse.ltk.core.refactoring.Refactoring;

import com.cevelop.intwidthfixator.refactorings.conversion.ConversionRefactoring;


public class ConversionRefactoringAllTest extends AbstractConversionRefactoringTest {

    @Override
    protected Refactoring createRefactoring() {

        return new ConversionRefactoring(getPrimaryCElementFromCurrentProject().get(), info.also(c -> {
            c.refactor_char = true;
            c.refactor_short = true;
            c.refactor_int = true;
            c.refactor_long = true;
            c.refactor_longlong = true;
        }));
    }
}
