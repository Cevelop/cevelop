package com.cevelop.ctylechecker.domain.types.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.cevelop.ctylechecker.common.ExpressionNames;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;
import com.cevelop.ctylechecker.domain.types.AddPrefixResolution;
import com.cevelop.ctylechecker.domain.types.AddSuffixResolution;
import com.cevelop.ctylechecker.domain.types.CaseTransformerResolution;
import com.cevelop.ctylechecker.domain.types.Expression;
import com.cevelop.ctylechecker.domain.types.ReplaceResolution;


public final class Expressions {

    public static final ISingleExpression IS_ALL_SMALL         = new Expression(ExpressionNames.IS_ALL_SMALL_NAME, "^[a-z0-9_$]+$");
    public static final ISingleExpression IS_ALL_BIG           = new Expression(ExpressionNames.IS_ALL_BIG_NAME, "^[^a-z]+");
    public static final ISingleExpression PASCAL_CASE          = new Expression(ExpressionNames.PASCAL_CASE_NAME, "^[A-Z][a-z]+(?:[A-Z][a-z]+)*$");
    public static final ISingleExpression SNAKE_CASE           = new Expression(ExpressionNames.SNAKE_CASE_NAME,
            "^[a-z](|[a-z0-9][_-]|[_-])(|[a-z0-9]+|[a-z0-9]+[_-](|[a-z0-9]+))+$");
    public static final ISingleExpression CPP_FILE_ENDING      = new Expression(ExpressionNames.CPP_FILE_ENDING_NAME, "(^(?!cpp$).*$)");
    public static final ISingleExpression CPP_CC_C_FILE_ENDING = new Expression(ExpressionNames.CPP_CC_C_FILE_ENDING_NAME,
            "(^(?!cpp$|cc$|C$|c\\+\\+$).*$)");
    public static final ISingleExpression CC_FILE_ENDING       = new Expression(ExpressionNames.CC_FILE_ENDING_NAME, "(^(?!cc$).*$)");
    public static final ISingleExpression H_FILE_ENDING        = new Expression(ExpressionNames.H_FILE_ENDING_NAME, "(^(?!h$).*$)");
    public static final ISingleExpression H_HPP_FILE_ENDING    = new Expression(ExpressionNames.H_HPP_FILE_ENDING_NAME, "(^(?!h$|hpp$).*$)");
    public static final ISingleExpression CONST_CASE           = new Expression(ExpressionNames.CONST_CASE_NAME,
            "^[A-Z](|[A-Z0-9][_]|[_])(|[A-Z0-9]+|[A-Z0-9]+[_](|[A-Z0-9]+))+$");
    public static final ISingleExpression CAMEL_CASE           = new Expression(ExpressionNames.CAMEL_CASE_NAME,
            "[a-z]+((\\d)|([A-Z0-9][a-z0-9]+))*([A-Z])?");
    public static final ISingleExpression HAS_PREFIX           = new Expression(ExpressionNames.HAS_PREFIX_NAME, "^(%s)(\\w+)+$");
    public static final ISingleExpression HAS_SUFFIX           = new Expression(ExpressionNames.HAS_SUFFIX_NAME, "^(\\w+)+(%s)$");

    public static final ISingleExpression POINT_SPLIT             = new Expression(ExpressionNames.POINT_SPLIT_NAME, "\\.");
    public static final ISingleExpression SNAKE_CASE_SPLIT        = new Expression(ExpressionNames.SNAKE_CASE_SPLIT_NAME, "_");
    public static final ISingleExpression PASCAL_CAMEL_CASE_SPLIT = new Expression(ExpressionNames.PASCAL_CAMEL_CASE_SPLIT_NAME,
            "(?=[A-Z][a-z])|(?<=[a-z])(?=[A-Z])");
    public static final ISingleExpression DASH_SPLIT              = new Expression(ExpressionNames.DASH_SPLIT_NAME, "-");

    static {
        CONST_CASE.setResolution(new CaseTransformerResolution(CONST_CASE));
        CONST_CASE.setOrder(OrderPriority.HIGH);
        PASCAL_CASE.setResolution(new CaseTransformerResolution(PASCAL_CASE));
        PASCAL_CASE.setOrder(OrderPriority.HIGH);
        SNAKE_CASE.setResolution(new CaseTransformerResolution(SNAKE_CASE));
        SNAKE_CASE.setOrder(OrderPriority.HIGH);
        CAMEL_CASE.setResolution(new CaseTransformerResolution(CAMEL_CASE));
        CAMEL_CASE.setOrder(OrderPriority.HIGH);

        CC_FILE_ENDING.shouldMatch(false);
        CC_FILE_ENDING.setResolution(new ReplaceResolution(CC_FILE_ENDING));
        CC_FILE_ENDING.setArgument("cc");
        CC_FILE_ENDING.setOrder(OrderPriority.LOW);

        CPP_FILE_ENDING.shouldMatch(false);
        CPP_FILE_ENDING.setResolution(new ReplaceResolution(CPP_FILE_ENDING));
        CPP_FILE_ENDING.setArgument("cpp");
        CPP_FILE_ENDING.setOrder(OrderPriority.LOW);

        CPP_CC_C_FILE_ENDING.shouldMatch(false);
        CPP_CC_C_FILE_ENDING.setResolution(new ReplaceResolution(CPP_CC_C_FILE_ENDING));
        CPP_CC_C_FILE_ENDING.setArgument("c++");
        CPP_CC_C_FILE_ENDING.setOrder(OrderPriority.LOW);

        H_FILE_ENDING.shouldMatch(false);
        H_FILE_ENDING.setResolution(new ReplaceResolution(H_FILE_ENDING));
        H_FILE_ENDING.setArgument("h");
        H_FILE_ENDING.setOrder(OrderPriority.LOW);

        H_HPP_FILE_ENDING.shouldMatch(false);
        H_HPP_FILE_ENDING.setResolution(new ReplaceResolution(H_HPP_FILE_ENDING));
        H_HPP_FILE_ENDING.setArgument("hpp");
        H_HPP_FILE_ENDING.setOrder(OrderPriority.LOW);

        IS_ALL_SMALL.setResolution(new CaseTransformerResolution(IS_ALL_SMALL));
        IS_ALL_SMALL.setOrder(OrderPriority.HIGH);
        IS_ALL_BIG.setResolution(new CaseTransformerResolution(IS_ALL_BIG));
        IS_ALL_BIG.setOrder(OrderPriority.HIGH);

        HAS_PREFIX.setResolution(new AddPrefixResolution(HAS_PREFIX));
        HAS_PREFIX.setOrder(OrderPriority.LOW);
        HAS_SUFFIX.setResolution(new AddSuffixResolution(HAS_SUFFIX));
        HAS_SUFFIX.setOrder(OrderPriority.LOW);
    }

    public static List<ISingleExpression> all() {
        return Arrays.asList(PASCAL_CASE, SNAKE_CASE, CONST_CASE, CAMEL_CASE, HAS_PREFIX, HAS_SUFFIX, IS_ALL_SMALL, IS_ALL_BIG, CPP_FILE_ENDING,
                H_FILE_ENDING, CC_FILE_ENDING, CPP_CC_C_FILE_ENDING, H_HPP_FILE_ENDING);
    }

    public static Optional<ISingleExpression> find(String expressionName) {
        for (ISingleExpression expression : all()) {
            if (expression.getName().equals(expressionName)) {
                return Optional.of(expression);
            }
        }
        return Optional.empty();
    }
}
