package com.cevelop.ctylechecker.ids;

import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class IdHelper {

    public static final String PLUGIN_ID   = FrameworkUtil.getBundle(IdHelper.class).getSymbolicName();
    public static final String PLUGIN_NAME = FrameworkUtil.getBundle(IdHelper.class).getHeaders().get(Constants.BUNDLE_NAME);

    public static final String DEFAULT_QUALIFIER = PLUGIN_ID;

    public static final String PREFERENCES_PREFIX = DEFAULT_QUALIFIER + ".preferences.";
    public static final String PROBLEMS_PREFIX    = DEFAULT_QUALIFIER + ".problems.";

    public static final String P_GENERAL_SETTINGS                      = DEFAULT_QUALIFIER + ".generalSettings";                  //$NON-NLS-1$
    public static final String P_PATTERNS_PROP_AND_PREF_PAGE_QUALIFIER = PREFERENCES_PREFIX + "PatternPropertyAndPreferencePage"; //$NON-NLS-1$

    public static final String CTYLECHECKER_PREFERENCES_PAGE_ID = PREFERENCES_PREFIX + "CtylecheckerPreferencePage";
    public static final String CTYLECHECKER_PROPERTIES_PAGE_ID  = PREFERENCES_PREFIX + "CtylecheckerPropertiesPage";

    public static final String CODAN_PREFERENCES_PAGE_ID = "org.eclipse.cdt.codan.ui.preferences.CodanPreferencePage";
    public static final String CODAN_PROPERTIES_PAGE_ID  = "org.eclipse.cdt.codan.ui.properties.codanProblemsPropertyPage";

    public static final String MARKER_ID = DEFAULT_QUALIFIER + ".marker.stylemarker";

    public enum ProblemId implements IProblemId<ProblemId> {

        EXPLICIT_CONSTRUCTOR(PROBLEMS_PREFIX + "constructorshouldbeexplicit"), //
        MEMBER_INITIALIZER_UNUSED(PROBLEMS_PREFIX + "memberinitializernotused"), //
        REDUNDANT_ACCESS_SPECIFIER(PROBLEMS_PREFIX + "redundantaccessspecifier"), //
        MULTIPLE_ASSERTS(PROBLEMS_PREFIX + "multipleasserts"), //
        IOSTREAM(PROBLEMS_PREFIX + "iostream"), //
        CIN_COUT(PROBLEMS_PREFIX + "cincout"), //
        USING_DIRECTIVE(PROBLEMS_PREFIX + "using"), //
        MISSING_STD_INCLUDE(PROBLEMS_PREFIX + "missingstandardinclude"), //
        INCLUDE_GUARD_MISSING(PROBLEMS_PREFIX + "includeguard"), //
        NON_SYS_INCLUDES_FIRST(PROBLEMS_PREFIX + "nonsystemincludesfirst"), //
        SELF_INCLUDE_POSITION(PROBLEMS_PREFIX + "selfincludeposition"), //
        MISSING_SELF_INCLUDE(PROBLEMS_PREFIX + "missingselfinclude"), //
        SUPERFLUOUS_STD_INCLUDE(PROBLEMS_PREFIX + "superfluousstandardinclude"), //
        GLOBAL_NON_CONST_VAR(PROBLEMS_PREFIX + "globalnonconstvar"), //
        DYNAMIC(PROBLEMS_PREFIX + "dynamicstyleproblem"), //
        DYNAMIC_FOR_FILES(PROBLEMS_PREFIX + "dynamicstyleproblemforfiles");//

        public final String id;

        ProblemId(final String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return id;
        }

        public static ProblemId of(String string) {
            for (final ProblemId id : values()) {
                if (id.getId().equals(string)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("Illegal ProblemId: " + string);
        }

        @Override
        public ProblemId unstringify(String string) {
            return of(string);
        }

        @Override
        public String stringify() {
            return id;
        }

    }

}
