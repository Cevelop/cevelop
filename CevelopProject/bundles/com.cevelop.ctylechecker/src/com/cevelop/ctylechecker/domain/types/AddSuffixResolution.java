package com.cevelop.ctylechecker.domain.types;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.domain.types.util.RenameTransformer;


public class AddSuffixResolution extends AbstractRenameResolution {

    public AddSuffixResolution(ISingleExpression expression) {
        super(expression);
    }

    @Override
    public String transform(String pName) {
        if (expression.getArgument().isEmpty()) {
            return pName;
        }
        if (expression.getHint() != null && ResolutionHint.casingHints().contains(expression.getHint())) {
            return hintTransform(pName, expression.getHint());
        } else {
            return blindTransform(pName);
        }
    }

    private String hintTransform(String pName, ResolutionHint hint) {
        RenameTransformer transformer = new RenameTransformer();
        String suffix = expression.getArgument();
        if (expression.getHint().equals(ResolutionHint.PASCAL_CASE)) {
            if (suffix.length() > 1) {
                suffix = transformer.transformToPascalCase(suffix);
            } else {
                suffix = transformer.transformToCamelCase(suffix);
            }
        } else if (expression.getHint().equals(ResolutionHint.CAMEL_CASE)) {
            suffix = transformer.transformToPascalCase(suffix);
        } else if (expression.getHint().equals(ResolutionHint.SNAKE_CASE)) {
            if ((expression.getArgument().charAt(0) + "").matches("^[a-zA-Z]+")) {
                suffix = "_" + transformer.transformToSnakeCase(suffix);
            } else {
                char firstChar = suffix.charAt(0);
                suffix = firstChar + transformer.transformToSnakeCase(suffix);
            }
        } else if (expression.getHint().equals(ResolutionHint.CONST_CASE)) {
            if ((expression.getArgument().charAt(0) + "").matches("^[a-zA-Z]+")) {
                suffix = "_" + transformer.transformToConstCase(suffix);
            } else {
                char firstChar = suffix.charAt(0);
                suffix = firstChar + transformer.transformToConstCase(suffix);
            }
        } else if (expression.getHint().equals(ResolutionHint.ALL_SMALL_CASE)) {
            suffix = transformer.transformToAllSmallCase(suffix);
        } else if (expression.getHint().equals(ResolutionHint.ALL_BIG_CASE)) {
            suffix = transformer.transformToAllBigCase(suffix);
        } else {
            return blindTransform(pName);
        }
        return pName + suffix;
    }

    private String blindTransform(String pName) {
        RenameTransformer transformer = new RenameTransformer();
        String suffix = expression.getArgument();
        if (Expressions.SNAKE_CASE.check(pName)) {
            if ((expression.getArgument().charAt(0) + "").matches("^[a-zA-Z]+")) {
                suffix = "_" + transformer.transformToAllSmallCase(suffix);
            } else {
                char firstChar = suffix.charAt(0);
                suffix = firstChar + transformer.transformToAllSmallCase(suffix);
            }
        } else if (Expressions.CAMEL_CASE.check(pName)) {
            suffix = transformer.transformToPascalCase(suffix);
        } else if (Expressions.PASCAL_CASE.check(pName)) {
            suffix = transformer.transformToPascalCase(suffix);
        } else if (Expressions.CONST_CASE.check(pName)) {
            if ((expression.getArgument().charAt(0) + "").matches("^[a-zA-Z]+")) {
                suffix = "_" + transformer.transformToConstCase(suffix);
            } else {
                char firstChar = suffix.charAt(0);
                suffix = firstChar + transformer.transformToConstCase(suffix);
            }
        } else if (Expressions.IS_ALL_SMALL.check(pName)) {
            suffix = transformer.transformToAllSmallCase(suffix);
        } else if (Expressions.IS_ALL_BIG.check(pName)) {
            suffix = transformer.transformToAllBigCase(suffix);
        }
        return pName + suffix;
    }
}
