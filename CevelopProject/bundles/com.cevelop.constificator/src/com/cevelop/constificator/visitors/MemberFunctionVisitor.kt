package com.cevelop.constificator.visitors

import org.eclipse.cdt.core.dom.ast.IASTDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod
import com.cevelop.constificator.checkers.Configuration
import com.cevelop.constificator.checkers.ProblemId
import com.cevelop.constificator.core.deciders.classmembers.MemberFunctionDecider
import com.cevelop.constificator.core.util.structural.descendsFrom
import com.cevelop.constificator.core.util.type.Truelean
import com.cevelop.constificator.core.util.type.enumSetOf
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor

class MemberFunctionVisitor(reporter: ISimpleReporter<ProblemId>) : SimpleVisitor<ProblemId, Configuration>(reporter) {
    init {
        shouldVisitDeclarators = true
    }

    override fun getProblemIds() = enumSetOf<ProblemId>()

    override fun visit(declarator: IASTDeclarator?): Int {
        if (declarator !is ICPPASTFunctionDeclarator) {
            return PROCESS_CONTINUE
        }

        if (declarator.descendsFrom<ICPPASTFunctionDefinition>()) {
            declarator.name?.resolveBinding()?.let {
                if (it is ICPPMethod) {
                    MemberFunctionDecider.canConstify(declarator).let { decision ->
                        if (decision.get() !== Truelean.NO) {
                            reporter.addNodeForReporting(DecisionReport(decision))
                        }
                    }
                }
            }
        }

        return PROCESS_CONTINUE
    }
}