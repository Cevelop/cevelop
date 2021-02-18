package com.cevelop.ctylechecker.domain.types;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.domain.types.util.RenameTransformer;


public class CaseTransformerResolution extends AbstractRenameResolution {

    public CaseTransformerResolution(ISingleExpression expression) {
        super(expression);
    }

    public String transform(String name) {
        RenameTransformer transformer = new RenameTransformer();
        if (expression.equals(Expressions.SNAKE_CASE)) {
            name = transformer.transformToSnakeCase(name);
        }
        if (expression.equals(Expressions.CAMEL_CASE)) {
            name = transformer.transformToCamelCase(name);
        }
        if (expression.equals(Expressions.PASCAL_CASE)) {
            name = transformer.transformToPascalCase(name);
        }
        if (expression.equals(Expressions.CONST_CASE)) {
            name = transformer.transformToConstCase(name);
        }
        if (expression.equals(Expressions.IS_ALL_SMALL)) {
            name = transformer.transformToAllSmallCase(name);
        }
        if (expression.equals(Expressions.IS_ALL_BIG)) {
            name = transformer.transformToAllBigCase(name);
        }
        return name;
    }
}
