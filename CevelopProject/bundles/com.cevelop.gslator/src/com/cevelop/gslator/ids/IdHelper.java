package com.cevelop.gslator.ids;

import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ids.IId;


public class IdHelper {

    public static final String PLUGIN_ID         = FrameworkUtil.getBundle(IdHelper.class).getSymbolicName();
    public static final String DEFAULT_QUALIFIER = PLUGIN_ID;

    public static final String PREFERENCES_PREFIX = DEFAULT_QUALIFIER + ".preferences.";
    public static final String PROBLEMS_PREFIX    = DEFAULT_QUALIFIER + ".problems.";

    public enum ProblemId implements IProblemId<ProblemId> {

        P_C164(PROBLEMS_PREFIX + Rule.C164 + "AvoidConversionOperators"), //
        P_C20(PROBLEMS_PREFIX + Rule.C20 + "RedundantOperations"), //
        P_C21(PROBLEMS_PREFIX + Rule.C21 + "MissingSpecialMemberFunctions"), //
        P_C31_01(PROBLEMS_PREFIX + Rule.C31 + "_01NoDestructor"), //
        P_C31_02(PROBLEMS_PREFIX + Rule.C31 + "_02DestructorHasNoBody"), //
        P_C31_03(PROBLEMS_PREFIX + Rule.C31 + "_03DestructorWithMissingDeleteStatements"), //
        P_C35(PROBLEMS_PREFIX + Rule.C35 + "BaseClassDestructor"), //
        P_C37(PROBLEMS_PREFIX + Rule.C37 + "DestructorShouldBeNoExcept"), //
        P_C44(PROBLEMS_PREFIX + Rule.C44 + "NoexceptCtor"), //
        P_C45(PROBLEMS_PREFIX + Rule.C45 + "InClassInitialize"), //
        P_C46(PROBLEMS_PREFIX + Rule.C46 + "DeclareSingleCtorExplicit"), //
        P_C47(PROBLEMS_PREFIX + Rule.C47 + "InitializeMemVarsInRightOrder"), //
        P_C48(PROBLEMS_PREFIX + Rule.C48 + "PreferInClassInitializerToCtorInit"), //
        P_C49(PROBLEMS_PREFIX + Rule.C49 + "NoAssignmentsInCtor"), //
        P_C60_01(PROBLEMS_PREFIX + Rule.C60 + "_01CopyAssignmentNonVirtual"), //
        P_C60_02(PROBLEMS_PREFIX + Rule.C60 + "_02CopyAssignmentParameterByConstRef"), //
        P_C60_03(PROBLEMS_PREFIX + Rule.C60 + "_03CopyAssignmentReturnByNonConstRef"), //
        P_C63_01(PROBLEMS_PREFIX + Rule.C63 + "_01MoveAssignmentNonVirtual"), //
        P_C63_02(PROBLEMS_PREFIX + Rule.C63 + "_02MoveAssignmentReturnByNonConstRef"), //
        P_C66(PROBLEMS_PREFIX + Rule.C66 + "MoveOperationsNoExcept"), //
        P_C83(PROBLEMS_PREFIX + Rule.C83 + "ValueLikeTypesShouldHaveSwap"), //
        P_C84(PROBLEMS_PREFIX + Rule.C84 + "MakeSwapNoExcept"), //
        P_C85(PROBLEMS_PREFIX + Rule.C85 + "NamespaceLevelSwapFunction"), //
        P_ES09(PROBLEMS_PREFIX + Rule.ES09 + "AvoidALLCAPSnames"), //
        P_ES20(PROBLEMS_PREFIX + Rule.ES20 + "AlwaysInitializeAnObject"), //
        P_ES26(PROBLEMS_PREFIX + Rule.ES26 + "DontUseVariableForTwoUnrelatedPurposes"), //
        P_ES46(PROBLEMS_PREFIX + Rule.ES46 + "AvoidLossyConversions"), //
        P_ES49(PROBLEMS_PREFIX + Rule.ES49 + "IfMustUseNamedCast"), //
        P_ES50(PROBLEMS_PREFIX + Rule.ES50 + "DontCastAwayConst"), //
        P_ES74(PROBLEMS_PREFIX + Rule.ES74 + "DeclareLoopVariableInTheInitializer"), //
        P_ES75(PROBLEMS_PREFIX + Rule.ES75 + "AvoidDoStatements"), //
        P_ES76(PROBLEMS_PREFIX + Rule.ES76 + "AvoidGoto"), //
        ;//

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

    public enum Rule implements IId<Rule> {

        C164("C.164"), //
        C20("C.20"), //
        C21("C.21"), //
        C31("C.31"), //
        C35("C.35"), //
        C37("C.37"), //
        C44("C.44"), //
        C45("C.45"), //
        C46("C.46"), //
        C47("C.47"), //
        C48("C.48"), //
        C49("C.49"), //
        C60("C.60"), //
        C63("C.63"), //
        C66("C.66"), //
        C83("C.83"), //
        C84("C.84"), //
        C85("C.85"), //
        ES09("ES.09"), //
        ES20("ES.20"), //
        ES26("ES.26"), //
        ES46("ES.46"), //
        ES49("ES.49"), //
        ES50("ES.50"), //
        ES74("ES.74"), //
        ES75("ES.75"), //
        ES76("ES.76"),//
        ;//

        public final String id;

        Rule(final String id) {
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

        public static Rule of(String string) {
            for (final Rule id : values()) {
                if (id.getId().equals(string)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("Illegal Rule: " + string);
        }

        @Override
        public Rule unstringify(String string) {
            return of(string);
        }

        @Override
        public String stringify() {
            return id;
        }

    }

}
