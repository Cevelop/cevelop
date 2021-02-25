package com.cevelop.templator.tests.integrationtest.resolution.template;

import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplatorMultiLevelResolutionTest;


public class VariableTemplateTest extends TemplatorMultiLevelResolutionTest {

    @Test
    public void fibonacci_4() throws TemplatorException {
        statementMatchesExpectedDefinition(getSubnameInMain(0).getDefinition(), 0);
    }

    @Test
    public void fibonacci_4_1() throws TemplatorException {
        AbstractResolvedNameInfo fibo3 = getSubname(getSubnameInMain(0), 0);
        statementMatchesExpectedDefinition(fibo3.getDefinition(), 0);
    }

    @Test
    public void fibonacci_4_2() throws TemplatorException {
        AbstractResolvedNameInfo fibo2 = getSubname(getSubnameInMain(0), 1);
        statementMatchesExpectedDefinition(fibo2.getDefinition(), 0);
    }

    @Test
    public void fibonacci_3_1() throws TemplatorException {
        AbstractResolvedNameInfo fibo3 = getSubname(getSubnameInMain(0), 0);
        AbstractResolvedNameInfo fibo2 = getSubname(fibo3, 0);
        statementMatchesExpectedDefinition(fibo2.getDefinition(), 0);
    }

    @Test
    public void fibonacci_3_2() throws TemplatorException {
        AbstractResolvedNameInfo fibo3 = getSubname(getSubnameInMain(0), 0);
        AbstractResolvedNameInfo fibo1 = getSubname(fibo3, 1);
        statementMatchesExpectedDefinition(fibo1.getDefinition(), 1);
    }

    @Test
    public void fibonacci_2_1() throws TemplatorException {
        AbstractResolvedNameInfo fibo2 = getSubname(getSubnameInMain(0), 1);
        AbstractResolvedNameInfo fibo1 = getSubname(fibo2, 0);
        statementMatchesExpectedDefinition(fibo1.getDefinition(), 1);
    }

    @Test
    public void fibonacci_2_2() throws TemplatorException {
        AbstractResolvedNameInfo fibo2 = getSubname(getSubnameInMain(0), 1);
        AbstractResolvedNameInfo fibo0 = getSubname(fibo2, 1);
        statementMatchesExpectedDefinition(fibo0.getDefinition(), 2);
    }
}
