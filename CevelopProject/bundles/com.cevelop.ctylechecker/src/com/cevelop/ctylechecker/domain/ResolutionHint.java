package com.cevelop.ctylechecker.domain;

import java.util.Arrays;
import java.util.List;


public enum ResolutionHint {
    NONE, PREFERED, CAMEL_CASE, SNAKE_CASE, CONST_CASE, PASCAL_CASE, ALL_SMALL_CASE, ALL_BIG_CASE;

    public static List<ResolutionHint> nonCasingHints() {
        return Arrays.asList(NONE, PREFERED);
    }

    public static List<ResolutionHint> casingHints() {
        return Arrays.asList(NONE, CAMEL_CASE, SNAKE_CASE, CONST_CASE, PASCAL_CASE, ALL_SMALL_CASE, ALL_BIG_CASE);
    }

    public static List<ResolutionHint> allHints() {
        return Arrays.asList(NONE, PREFERED, CAMEL_CASE, SNAKE_CASE, CONST_CASE, PASCAL_CASE, ALL_SMALL_CASE, ALL_BIG_CASE);
    }
}
