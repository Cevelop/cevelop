package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.AddSuffixResolution;
import com.cevelop.ctylechecker.domain.types.Expression;


public class AddSuffixResolutionTest {

    @Test
    public void testSimpleSuffixAdded() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("k");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariableK", transformed);
    }

    @Test
    public void testSimpleSuffixAdded2() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("K");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariableK", transformed);
    }

    @Test
    public void testSimpleSuffixAdded3() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("k_");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariableK", transformed);
    }

    @Test
    public void testComplexSuffixAdded() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("$konstant");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariable$konstant", transformed);
    }

    @Test
    public void testComplexSuffixAdded2() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("$konstant");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariable$konstant", transformed);
    }

    @Test
    public void testComplexSuffixAdded3() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("konstant_suffix");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariableKonstantSuffix", transformed);
    }

    @Test
    public void testComplexSuffixAdded4() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("$konstant_suffix");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariable$konstantSuffix", transformed);
    }

    @Test

    public void testComplexSuffixAdded5() {
        ISingleExpression expression = new Expression("SUFFIX");
        expression.setArgument("_konstant_suffix");
        AddSuffixResolution resolution = new AddSuffixResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariableKonstantSuffix", transformed);
    }
}
