package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.AddPrefixResolution;
import com.cevelop.ctylechecker.domain.types.Expression;


public class AddPrefixResolutionTest {

    @Test
    public void testSimplePrefixAdded() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("k");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("kSomeVariable", transformed);
    }

    @Test
    public void testSimplePrefixAdded2() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("k_");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("kSomeVariable", transformed);
    }

    @Test
    public void testSimplePrefixAdded3() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("K");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("kSomeVariable", transformed);
    }

    @Test
    public void testComplexPrefixAdded() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("constant_");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("constantSomeVariable", transformed);
    }

    @Test
    public void testComplexPrefixAdded2() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("constant$");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("constant$SomeVariable", transformed);
    }

    @Test
    public void testComplexPrefixAdded3() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("$constant_prefix$");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("$constantPrefix$SomeVariable", transformed);
    }

    @Test
    public void testComplexPrefixAdded4() {
        ISingleExpression expression = new Expression("PREFIX");
        expression.setArgument("$CONSTANTPREFIX$");
        AddPrefixResolution resolution = new AddPrefixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("$constantprefix$SomeVariable", transformed);
    }
}
