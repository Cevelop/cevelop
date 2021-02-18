package com.cevelop.constificator.visitors

import com.cevelop.constificator.checkers.ProblemId
import com.cevelop.constificator.core.deciders.decision.Decision
import com.cevelop.constificator.core.util.type.Truelean
import ch.hsr.ifs.iltis.cpp.core.ast.checker.VisitorReport

data class DecisionReport(val decision: Decision) : VisitorReport<ProblemId>(
        if (decision.get() === Truelean.YES) {
            ProblemId.MISSING_QUALIFICATION
        } else {
            ProblemId.POTENTIALLY_MISSING_QUALIFICATION
        }, decision.node()) {
}