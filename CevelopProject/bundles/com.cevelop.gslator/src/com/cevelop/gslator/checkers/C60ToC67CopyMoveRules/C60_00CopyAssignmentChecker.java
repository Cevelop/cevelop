package com.cevelop.gslator.checkers.C60ToC67CopyMoveRules;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.Rule;


public abstract class C60_00CopyAssignmentChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-copy-assignment";

    @Override
    public Rule getRule() {
        return Rule.C60;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
