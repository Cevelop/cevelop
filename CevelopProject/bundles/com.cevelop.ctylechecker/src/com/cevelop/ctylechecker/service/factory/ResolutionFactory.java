package com.cevelop.ctylechecker.service.factory;

import java.util.Arrays;
import java.util.List;

import com.cevelop.ctylechecker.domain.IResolution;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.AddPrefixResolution;
import com.cevelop.ctylechecker.domain.types.AddSuffixResolution;
import com.cevelop.ctylechecker.domain.types.CaseTransformerResolution;
import com.cevelop.ctylechecker.domain.types.DefaultRenameResolution;
import com.cevelop.ctylechecker.domain.types.ReplaceResolution;


public class ResolutionFactory {

    public static final String DEFAULT_RENAME_RESOLUTION   = DefaultRenameResolution.class.getSimpleName();
    public static final String CASE_TRANSFORMER_RESOLUTION = CaseTransformerResolution.class.getSimpleName();
    public static final String ADD_PREFIX_RESOLUTION       = AddPrefixResolution.class.getSimpleName();
    public static final String ADD_SUFFIX_RESOLUTION       = AddSuffixResolution.class.getSimpleName();
    public static final String REPLACE_RESOLUTION          = ReplaceResolution.class.getSimpleName();

    public static List<String> names() {
        return Arrays.asList(//
                DEFAULT_RENAME_RESOLUTION, //
                CASE_TRANSFORMER_RESOLUTION, //
                ADD_PREFIX_RESOLUTION, //
                ADD_SUFFIX_RESOLUTION, //
                REPLACE_RESOLUTION);
    }

    public static DefaultRenameResolution createDefaultRenameResolution() {
        return new DefaultRenameResolution();
    }

    public static CaseTransformerResolution createCaseTransformerResolution(ISingleExpression pExpression) {
        return new CaseTransformerResolution(pExpression);
    }

    public static AddPrefixResolution createPrefixResolution(ISingleExpression pExpression) {
        return new AddPrefixResolution(pExpression);
    }

    public static AddSuffixResolution createSuffixResolution(ISingleExpression pExpression) {
        return new AddSuffixResolution(pExpression);
    }

    public static ReplaceResolution createReplaceResolution(ISingleExpression pExpression) {
        return new ReplaceResolution(pExpression);
    }

    public static String getType(IResolution pResolution) {
        if (pResolution instanceof DefaultRenameResolution) {
            return DEFAULT_RENAME_RESOLUTION;
        }
        if (pResolution instanceof CaseTransformerResolution) {
            return CASE_TRANSFORMER_RESOLUTION;
        }
        if (pResolution instanceof AddPrefixResolution) {
            return ADD_PREFIX_RESOLUTION;
        }
        if (pResolution instanceof AddSuffixResolution) {
            return ADD_SUFFIX_RESOLUTION;
        }
        if (pResolution instanceof ReplaceResolution) {
            return REPLACE_RESOLUTION;
        }
        return "";
    }

    public static IResolution createResolution(String pResolutionType, ISingleExpression pExpression) {
        if (pResolutionType.equals(CASE_TRANSFORMER_RESOLUTION)) {
            return createCaseTransformerResolution(pExpression);
        }
        if (pResolutionType.equals(ADD_PREFIX_RESOLUTION)) {
            return createPrefixResolution(pExpression);
        }
        if (pResolutionType.equals(ADD_SUFFIX_RESOLUTION)) {
            return createSuffixResolution(pExpression);
        }
        if (pResolutionType.equals(REPLACE_RESOLUTION)) {
            return createReplaceResolution(pExpression);
        }
        return createDefaultRenameResolution();
    }

    public static String getResolutionInfo(String pResolutionType) {
        if (pResolutionType.equals(ADD_PREFIX_RESOLUTION)) {
            return Messages.ADD_PREFIX_RESOLUTION_TEXT;
        }
        if (pResolutionType.equals(ADD_SUFFIX_RESOLUTION)) {
            return Messages.ADD_SUFFIX_RESOLUTION_TEXT;
        }
        if (pResolutionType.equals(REPLACE_RESOLUTION)) {
            return Messages.REPLACE_RESOLUTION_TEXT;
        }
        if (pResolutionType.equals(DEFAULT_RENAME_RESOLUTION)) {
            return Messages.DEFAULT_RENAME_RESOLUTION_TEXT;
        }
        return "";
    }
}
