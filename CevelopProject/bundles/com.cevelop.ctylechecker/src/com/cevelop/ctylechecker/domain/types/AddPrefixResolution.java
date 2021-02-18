package com.cevelop.ctylechecker.domain.types;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.domain.types.util.NameModel;
import com.cevelop.ctylechecker.domain.types.util.RenameTransformer;


public class AddPrefixResolution extends AbstractRenameResolution {

    private NameModel         nameModel   = new NameModel("", "");
    private RenameTransformer transformer = new RenameTransformer();

    public AddPrefixResolution(ISingleExpression expression) {
        super(expression);
    }

    private RenameTransformer getTransformer() {
        if (transformer == null) {
            transformer = new RenameTransformer();
        }
        return transformer;
    }

    private NameModel getModel() {
        if (nameModel == null) {
            nameModel = new NameModel("", "");
        }
        return nameModel;
    }

    @Override
    public String transform(String pName) {
        if (expression.getArgument().isEmpty()) return pName;
        nameModel = new NameModel(pName, expression.getArgument());
        if (expression.getHint() != null && ResolutionHint.casingHints().contains(expression.getHint())) {
            return hintTransform(expression.getHint());
        } else {
            return blindTransform();
        }
    }

    private String hintTransform(ResolutionHint hint) {
        if (expression.getHint().equals(ResolutionHint.PASCAL_CASE)) {
            toPascalCase();
        } else if (expression.getHint().equals(ResolutionHint.CAMEL_CASE)) {
            toCamelCase();
        } else if (expression.getHint().equals(ResolutionHint.SNAKE_CASE)) {
            toSnakeCase();
        } else if (expression.getHint().equals(ResolutionHint.CONST_CASE)) {
            toConstCase();
        } else if (expression.getHint().equals(ResolutionHint.ALL_SMALL_CASE)) {
            toAllSmallCase();
        } else if (expression.getHint().equals(ResolutionHint.ALL_BIG_CASE)) {
            toAllBigCase();
        } else {
            return blindTransform();
        }
        return getModel().supplement + getModel().name;
    }

    private void toAllBigCase() {
        getModel().name = getTransformer().transformToAllBigCase(getModel().name);
        toBlindAllBigCase();
    }

    private void toAllSmallCase() {
        getModel().name = getTransformer().transformToAllSmallCase(getModel().name);
        toBlindAllSmallCase();
    }

    private void toConstCase() {
        getModel().name = getTransformer().transformToConstCase(getModel().name);
        if ((getModel().supplement.charAt(getModel().supplement.length() - 1) + "").matches("^[a-zA-Z]+")) {
            getModel().supplement = getTransformer().transformToAllBigCase(getModel().supplement) + "_";
        } else {
            char lastChar = getModel().supplement.charAt(getModel().supplement.length() - 1);
            getModel().supplement = getTransformer().transformToAllBigCase(getModel().supplement) + lastChar;
        }
    }

    private void toSnakeCase() {
        getModel().name = getTransformer().transformToAllSmallCase(getModel().name);
        toBlindSnakeCase();
    }

    private void toCamelCase() {
        getModel().name = getTransformer().transformToPascalCase(getModel().name);
        toBlindAllSmallCase();
    }

    private void toPascalCase() {
        if (getModel().supplement.length() > 1) {
            getModel().name = getTransformer().transformToPascalCase(getModel().name);
            getModel().supplement = getTransformer().transformToPascalCase(getModel().supplement);
        } else {
            getModel().name = getTransformer().transformToCamelCase(getModel().name);
            getModel().supplement = getTransformer().transformToPascalCase(getModel().supplement);
        }
    }

    private String blindTransform() {
        if (Expressions.SNAKE_CASE.check(getModel().name)) {
            toBlindSnakeCase();
        } else if (Expressions.CAMEL_CASE.check(getModel().name)) {
            toBlindCamelCase();
        } else if (Expressions.PASCAL_CASE.check(getModel().name)) {
            toBlindPascalCase();
        } else if (Expressions.CONST_CASE.check(getModel().name)) {
            toBlindConstCase();
        } else if (Expressions.IS_ALL_SMALL.check(getModel().name)) {
            toBlindAllSmallCase();
        } else if (Expressions.IS_ALL_BIG.check(getModel().name)) {
            toBlindAllBigCase();
        }
        return getModel().supplement + getModel().name;
    }

    private void toBlindAllBigCase() {
        getModel().supplement = getTransformer().transformToAllBigCase(getModel().supplement);
    }

    private void toBlindAllSmallCase() {
        getModel().supplement = getTransformer().transformToAllSmallCase(getModel().supplement);
    }

    private void toBlindConstCase() {
        getModel().supplement = getTransformer().transformToConstCase(getModel().supplement);
    }

    private void toBlindPascalCase() {
        getModel().supplement = getTransformer().transformToPascalCase(getModel().supplement);
        getModel().name = getTransformer().transformToCamelCase(getModel().name);
    }

    private void toBlindCamelCase() {
        getModel().supplement = getTransformer().transformToCamelCase(getModel().supplement);
        getModel().name = getTransformer().transformToPascalCase(getModel().name);
    }

    private void toBlindSnakeCase() {
        if ((getModel().supplement.charAt(getModel().supplement.length() - 1) + "").matches("^[a-zA-Z]+")) {
            getModel().supplement = getTransformer().transformToAllSmallCase(getModel().supplement) + "_";
        } else {
            char lastChar = getModel().supplement.charAt(getModel().supplement.length() - 1);
            getModel().supplement = getTransformer().transformToAllSmallCase(getModel().supplement) + lastChar;
        }
    }
}
