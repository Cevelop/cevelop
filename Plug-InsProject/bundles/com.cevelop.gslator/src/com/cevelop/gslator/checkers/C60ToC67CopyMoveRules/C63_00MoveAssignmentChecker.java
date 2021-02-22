package com.cevelop.gslator.checkers.C60ToC67CopyMoveRules;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.Rule;


public abstract class C63_00MoveAssignmentChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-move-assignment";

    @Override
    public Rule getRule() {
        return Rule.C63;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
