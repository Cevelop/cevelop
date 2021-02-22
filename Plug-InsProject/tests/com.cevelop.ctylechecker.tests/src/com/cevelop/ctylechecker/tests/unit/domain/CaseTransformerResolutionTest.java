package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.CaseTransformerResolution;
import com.cevelop.ctylechecker.domain.types.util.Expressions;


public class CaseTransformerResolutionTest {

    @Test
    public void testTransformToSnakeCase() {
        ISingleExpression expression = Expressions.SNAKE_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("some_variable", transformed);
    }

    @Test
    public void testTransformToSnakeCase2() {
        ISingleExpression expression = Expressions.SNAKE_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SomeVariable";
        String transformed = resolution.transform(name);
        assertEquals("some_variable", transformed);
    }

    @Test
    public void testTransformToSnakeCase3() {
        ISingleExpression expression = Expressions.SNAKE_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SOME_VARIABLE";
        String transformed = resolution.transform(name);
        assertEquals("some_variable", transformed);
    }

    @Test
    public void testTransformToCamelCase() {
        ISingleExpression expression = Expressions.CAMEL_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "some_variable";
        String transformed = resolution.transform(name);
        assertEquals("someVariable", transformed);
    }

    @Test
    public void testTransformToCamelCase2() {
        ISingleExpression expression = Expressions.CAMEL_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SOME_VARIABLE";
        String transformed = resolution.transform(name);
        assertEquals("someVariable", transformed);
    }

    @Test
    public void testTransformToCamelCase3() {
        ISingleExpression expression = Expressions.CAMEL_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SomeVariable";
        String transformed = resolution.transform(name);
        assertEquals("someVariable", transformed);
    }

    @Test
    public void testTransformToPascalCase() {
        ISingleExpression expression = Expressions.PASCAL_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("SomeVariable", transformed);
    }

    @Test
    public void testTransformToPascalCase2() {
        ISingleExpression expression = Expressions.PASCAL_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "some_variable";
        String transformed = resolution.transform(name);
        assertEquals("SomeVariable", transformed);
    }

    @Test
    public void testTransformToPascalCase3() {
        ISingleExpression expression = Expressions.PASCAL_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SOME_VARIABLE";
        String transformed = resolution.transform(name);
        assertEquals("SomeVariable", transformed);
    }

    @Test
    public void testTransformToConstCase() {
        ISingleExpression expression = Expressions.CONST_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("SOME_VARIABLE", transformed);
    }

    @Test
    public void testTransformToConstCase2() {
        ISingleExpression expression = Expressions.CONST_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "some_variable";
        String transformed = resolution.transform(name);
        assertEquals("SOME_VARIABLE", transformed);
    }

    @Test
    public void testTransformToConstCase3() {
        ISingleExpression expression = Expressions.CONST_CASE;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SomeVariable";
        String transformed = resolution.transform(name);
        assertEquals("SOME_VARIABLE", transformed);
    }

    @Test
    public void testTransformToAllBigCase() {
        ISingleExpression expression = Expressions.IS_ALL_BIG;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("SOMEVARIABLE", transformed);
    }

    @Test
    public void testTransformToAllBigCase2() {
        ISingleExpression expression = Expressions.IS_ALL_BIG;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "some_variable";
        String transformed = resolution.transform(name);
        assertEquals("SOMEVARIABLE", transformed);
    }

    @Test
    public void testTransformToAllBigCase3() {
        ISingleExpression expression = Expressions.IS_ALL_BIG;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SomeVariable";
        String transformed = resolution.transform(name);
        assertEquals("SOMEVARIABLE", transformed);
    }

    @Test
    public void testTransformToAllSmallCase() {
        ISingleExpression expression = Expressions.IS_ALL_SMALL;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "someVariable";
        String transformed = resolution.transform(name);
        assertEquals("somevariable", transformed);
    }

    @Test
    public void testTransformToAllSmallCase2() {
        ISingleExpression expression = Expressions.IS_ALL_SMALL;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SOME_VARIABLE";
        String transformed = resolution.transform(name);
        assertEquals("somevariable", transformed);
    }

    @Test
    public void testTransformToAllSmallCase3() {
        ISingleExpression expression = Expressions.IS_ALL_SMALL;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "some_variable";
        String transformed = resolution.transform(name);
        assertEquals("somevariable", transformed);
    }

    @Test
    public void testTransformToAllSmallCase4() {
        ISingleExpression expression = Expressions.IS_ALL_SMALL;
        CaseTransformerResolution resolution = new CaseTransformerResolution(expression);
        String name = "SOMEVARIABLE";
        String transformed = resolution.transform(name);
        assertEquals("somevariable", transformed);
    }
}
