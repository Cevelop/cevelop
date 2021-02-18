package com.cevelop.gslator.checkers.C30ToC37DestructorRules;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.Rule;


public abstract class C31_00DeleteOwnersInDestructorChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-dtor-release";

    @Override
    public Rule getRule() {
        return Rule.C31;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
