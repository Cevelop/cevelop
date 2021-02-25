package com.cevelop.namespactor.helpers;

import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class IdHelper {

    public static final String PLUGIN_ID         = FrameworkUtil.getBundle(IdHelper.class).getSymbolicName();
    public static final String DEFAULT_QUALIFIER = PLUGIN_ID;

    public static final String PREFERENCES_PREFIX = DEFAULT_QUALIFIER + ".preferences.";
    public static final String PROBLEMS_PREFIX    = DEFAULT_QUALIFIER + ".problems.";

    public enum ProblemId implements IProblemId<ProblemId> {

        UDIR_IN_HEADER_PROBLEM_ID(PROBLEMS_PREFIX + "UDIRInHeader"), //
        UDIR_UNQUALIFIED_PROBLEM_ID(PROBLEMS_PREFIX + "UDIRUnqualified"), //
        UDEC_IN_HEADER_PROBLEM_ID(PROBLEMS_PREFIX + "UDECInHeader"), //
        UDIR_BEFORE_INCLUDE_PROBLEM_ID(PROBLEMS_PREFIX + "UDIRBeforeInclude"), //
        UDEC_BEFORE_INCLUDE_PROBLEM_ID(PROBLEMS_PREFIX + "UDECBeforeInclude"), //
        TYPEDEF_SHOULD_BE_ALIAS(PROBLEMS_PREFIX + "Typedef2Alias");//d

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
