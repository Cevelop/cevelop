package com.cevelop.intwidthfixator.helpers;

import org.eclipse.osgi.util.NLS;
import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ids.IRefactoringId;


/**
 * @author tstauber
 */
public class IdHelper {

    public static final String PLUGIN_ID         = FrameworkUtil.getBundle(IdHelper.class).getSymbolicName();
    public static final String DEFAULT_QUALIFIER = PLUGIN_ID;

    /* Predefined preference IDs */
    public static final String P_GENERAL_SETTINGS                     = DEFAULT_QUALIFIER + ".generalSettings";                       //$NON-NLS-1$
    public static final String PROPERTY_AND_PREFERENCE_PAGE_QUALIFIER = DEFAULT_QUALIFIER + ".preferences.PropertyAndPreferencePage"; //$NON-NLS-1$
    public static final String P_CHAR_MAPPING                         = DEFAULT_QUALIFIER + ".charMappingLength";                     //$NON-NLS-1$
    public static final String P_SHORT_MAPPING                        = DEFAULT_QUALIFIER + ".shortMappingLength";                    //$NON-NLS-1$
    public static final String P_INT_MAPPING                          = DEFAULT_QUALIFIER + ".intMappingLength";                      //$NON-NLS-1$
    public static final String P_LONG_MAPPING                         = DEFAULT_QUALIFIER + ".longMappingLength";                     //$NON-NLS-1$
    public static final String P_LONGLONG_MAPPING                     = DEFAULT_QUALIFIER + ".longlongMappingLength";                 //$NON-NLS-1$
    public static final String P_CHAR_PLATFORM_SIGNED_UNSIGNED        = DEFAULT_QUALIFIER + ".charSignedness";                        //$NON-NLS-1$

    /* Do not use directly. Use with ProblemId */
    public static final String Intern_P_PROBLEM_CASTS     = DEFAULT_QUALIFIER + ".problems.casts.not-fixed-width";     //$NON-NLS-1$
    public static final String Intern_P_PROBLEM_FUNCTIONS = DEFAULT_QUALIFIER + ".problems.functions.not-fixed-width"; //$NON-NLS-1$
    public static final String Intern_P_PROBLEM_TEMPLATES = DEFAULT_QUALIFIER + ".problems.templates.not-fixed-width"; //$NON-NLS-1$
    public static final String Intern_P_PROBLEM_TYPEDEFS  = DEFAULT_QUALIFIER + ".problems.typedefs.not-fixed-width";  //$NON-NLS-1$
    public static final String Intern_P_PROBLEM_VARIABLES = DEFAULT_QUALIFIER + ".problems.variables.not-fixed-width"; //$NON-NLS-1$

    /* Predefined values that can be assigned to the preference IDs */
    public static final String V_CHAR_PLATFORM_SIGNED   = "SIGNED";   //$NON-NLS-1$
    public static final String V_CHAR_PLATFORM_UNSIGNED = "UNSIGNED"; //$NON-NLS-1$

    /* Do not use directly. Use with WidthId */
    public static final String Intern_V_WIDTH_8  = "WIDTH_8";  //$NON-NLS-1$
    public static final String Intern_V_WIDTH_16 = "WIDTH_16"; //$NON-NLS-1$
    public static final String Intern_V_WIDTH_32 = "WIDTH_32"; //$NON-NLS-1$
    public static final String Intern_V_WIDTH_64 = "WIDTH_64"; //$NON-NLS-1$

    /* Enums for better comparison */
    public enum ProblemId implements IProblemId<ProblemId> {
      //@formatter:off
      CASTS(Intern_P_PROBLEM_CASTS),
      FUNCTION(Intern_P_PROBLEM_FUNCTIONS),
      TEMPLATE(Intern_P_PROBLEM_TEMPLATES),
      TYPEDEF(Intern_P_PROBLEM_TYPEDEFS),
      VARIABLES(Intern_P_PROBLEM_VARIABLES);
      //@formatter:on

        public final String id;

        @Override
        public String getId() {
            return id;
        }

        ProblemId(final String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }

        public static ProblemId of(String string) {
            for (final ProblemId problemId : values()) {
                if (problemId.getId().equals(string)) {
                    return problemId;
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

    public enum RefactoringId implements IRefactoringId<RefactoringId> {
      //@formatter:off
      INVERSION(DEFAULT_QUALIFIER + ".refactoring.inversion.InversionRefactoring"),
      CONVERSION(DEFAULT_QUALIFIER + ".refactoring.conversion.ConversionRefactoring");
      //@formatter:on

        public final String id;

        @Override
        public String getId() {
            return id;
        }

        RefactoringId(final String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }

        public static RefactoringId of(String string) {
            for (final RefactoringId refactoringId : values()) {
                if (refactoringId.getId().equals(string)) {
                    return refactoringId;
                }
            }
            throw new IllegalArgumentException("Illegal RefactoringId: " + string);
        }

        @Override
        public RefactoringId unstringify(String string) {
            return of(string);
        }

        @Override
        public String stringify() {
            return id;
        }
    }

    public enum WidthId {
      //@formatter:off
      WIDTH_8(Intern_V_WIDTH_8,8),
      WIDTH_16(Intern_V_WIDTH_16,16),
      WIDTH_32(Intern_V_WIDTH_32,32),
      WIDTH_64(Intern_V_WIDTH_64,64);
      //@formatter:on

        public final String id;
        public final int    width;

        WidthId(final String id, final int width) {
            this.id = id;
            this.width = width;
        }

        @Override
        public String toString() {
            return id;
        }

        public static WidthId valueOf(final int width) throws IllegalArgumentException {
            for (final WidthId widthId : values()) {
                if (widthId.width == width) {
                    return widthId;
                }
            }
            throw new IllegalArgumentException(NLS.bind(Messages.IdHelper_Exception, width));
        }

    }

    /* DialogSettings PLUGIN_ID prefixes */

    public static final String DS_CONVERSION_REFACTORING_PREFIX = DEFAULT_QUALIFIER + ".refactorings.inputPage.conversion"; //$NON-NLS-1$
    public static final String DS_INVERSION_REFACTORING_PREFIX  = DEFAULT_QUALIFIER + ".refactorings.inputPage.inversion";  //$NON-NLS-1$

}
