package com.cevelop.charwars.constants;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public enum ProblemId implements IProblemId<ProblemId> {

   //@formatter:off
	ARRAY_PROBLEM("com.cevelop.charwars.problems.ArrayProblem"),
	C_STRING_PROBLEM("com.cevelop.charwars.problems.CStringProblem"),
	C_STRING_ALIAS_PROBLEM("com.cevelop.charwars.problems.CStringAliasProblem"),
	C_STRING_CLEANUP_PROBLEM("com.cevelop.charwars.problems.CStringCleanupProblem"),
	C_STR_PROBLEM("com.cevelop.charwars.problems.CStrProblem"),
	POINTER_PARAMETER_PROBLEM("com.cevelop.charwars.problems.PointerParameterProblem"),
	C_STRING_PARAMETER_PROBLEM("com.cevelop.charwars.problems.CStringParameterProblem");
    //@formatter:on

    String id;

    ProblemId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static ProblemId of(String string) {
        for (final ProblemId problemId : values()) {
            if (problemId.getId().equals(string)) {
                return problemId;
            }
        }
        throw new IllegalArgumentException("Illegal ProblemIDs: " + string);
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
