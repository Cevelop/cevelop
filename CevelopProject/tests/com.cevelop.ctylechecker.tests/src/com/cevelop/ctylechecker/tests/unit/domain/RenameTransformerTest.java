package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.types.util.RenameTransformer;


public class RenameTransformerTest {

    @Test
    public void testNameIsTransformedFromCamelCaseToSnakeCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "aNameToBeSnakeCase";
        String transformedName = transformer.transformToSnakeCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("aname_to_be_snake_case", transformedName);
    }

    @Test
    public void testNameIsTransformedFromPascalCaseToSnakeCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NameToBeSnakeCase";
        String transformedName = transformer.transformToSnakeCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("name_to_be_snake_case", transformedName);
    }

    @Test
    public void testNameIsTransformedFromBigCaseToSnakeCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAMETOBESNAKECASE";
        String transformedName = transformer.transformToSnakeCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesnakecase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromConstCaseToSnakeCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAME_TO_BE_SNAKE_CASE";
        String transformedName = transformer.transformToSnakeCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("name_to_be_snake_case", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSmallCaseToSnakeCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nametobesnakecase";
        String transformedName = transformer.transformToSnakeCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesnakecase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSnakeCaseToCamelCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "name_to_be_camel_case";
        String transformedName = transformer.transformToCamelCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nameToBeCamelCase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromPascalCaseToCamelCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NameToBeCamelCase";
        String transformedName = transformer.transformToCamelCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nameToBeCamelCase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromBigCaseToCamelCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAMETOBECAMELCASE";
        String transformedName = transformer.transformToCamelCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobecamelcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromConstCaseToCamelCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAME_TO_BE_CAMEL_CASE";
        String transformedName = transformer.transformToCamelCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nameToBeCamelCase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSmallCaseToCamelCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nametobecamelcase";
        String transformedName = transformer.transformToCamelCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobecamelcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSnakeCaseToPascalCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "name_to_be_pascal_case";
        String transformedName = transformer.transformToPascalCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NameToBePascalCase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromCamelCaseToPascalCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nameToBePascalCase";
        String transformedName = transformer.transformToPascalCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NameToBePascalCase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromBigCaseToPascalCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAMETOBEPASCALCASE";
        String transformedName = transformer.transformToPascalCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("Nametobepascalcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromConstCaseToPascalCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAME_TO_BE_PASCAL_CASE";
        String transformedName = transformer.transformToPascalCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NameToBePascalCase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSmallCaseToPascalCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nametobepascalcase";
        String transformedName = transformer.transformToPascalCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("Nametobepascalcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromCamelCaseToAllBigCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nameToBeBigCase";
        String transformedName = transformer.transformToAllBigCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBEBIGCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSnakeCaseToAllBigCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "name_to_be_big_case";
        String transformedName = transformer.transformToAllBigCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBEBIGCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromPascalCaseToAllBigCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NameToBeBigCase";
        String transformedName = transformer.transformToAllBigCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBEBIGCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromConstCaseToAllBigCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAME_TO_BE_BIG_CASE";
        String transformedName = transformer.transformToAllBigCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBEBIGCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSmallCaseToAllBigCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nametobebigcase";
        String transformedName = transformer.transformToAllBigCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBEBIGCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromCamelCaseToConstCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nameToBeConstCase";
        String transformedName = transformer.transformToConstCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAME_TO_BE_CONST_CASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSnakeCaseToConstCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "name_to_be_const_case";
        String transformedName = transformer.transformToConstCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAME_TO_BE_CONST_CASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromPascalCaseToConstCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NameToBeConstCase";
        String transformedName = transformer.transformToConstCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAME_TO_BE_CONST_CASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromBigCaseToConstCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAMETOBECONSTCASE";
        String transformedName = transformer.transformToConstCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBECONSTCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSmallCaseToConstCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nametobeconstcase";
        String transformedName = transformer.transformToConstCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("NAMETOBECONSTCASE", transformedName);
    }

    @Test
    public void testNameIsTransformedFromCamelCaseToAllSmallCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "nameToBeSmallCase";
        String transformedName = transformer.transformToAllSmallCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesmallcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromPascalCaseToAllSmallCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NameToBeSmallCase";
        String transformedName = transformer.transformToAllSmallCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesmallcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromSnakeCaseToAllSmallCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "name_to_be_small_case";
        String transformedName = transformer.transformToAllSmallCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesmallcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromConstCaseToAllSmallCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAME_TO_BE_SMALL_CASE";
        String transformedName = transformer.transformToAllSmallCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesmallcase", transformedName);
    }

    @Test
    public void testNameIsTransformedFromBigCaseToAllSmallCase() {
        RenameTransformer transformer = new RenameTransformer();
        String nameToTransform = "NAMETOBESMALLCASE";
        String transformedName = transformer.transformToAllSmallCase(nameToTransform);
        assertTrue(!transformedName.isEmpty());
        assertEquals("nametobesmallcase", transformedName);
    }
}
