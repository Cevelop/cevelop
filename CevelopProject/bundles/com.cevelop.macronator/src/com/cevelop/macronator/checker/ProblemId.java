package com.cevelop.macronator.checker;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public enum ProblemId implements IProblemId<ProblemId> {

   //@formatter:off
   FUN_LIKE_MACRO("com.cevelop.macronator.plugin.ObsoleteFunctionLikeMacro"),
   UNUSED_MACRO("com.cevelop.macronator.plugin.UnusedMacro"),
   OBJECT_LIKE_MACRO("com.cevelop.macronator.plugin.ObsoleteObjectLikeMacro");
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
