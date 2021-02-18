package com.cevelop.constificator.checkers

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy
import com.cevelop.constificator.checkers.Preferences.Preference
import com.cevelop.constificator.core.deciders.decision.FunctionParameterDecision
import com.cevelop.constificator.core.deciders.decision.LocalVariableDecision
import com.cevelop.constificator.core.deciders.decision.MemberFunctionDecision
import com.cevelop.constificator.core.deciders.decision.MemberVariableDecision
import com.cevelop.constificator.visitors.DecisionReport
import com.cevelop.constificator.visitors.FunctionParametersVisitor
import com.cevelop.constificator.visitors.LocalVariablesVisitor
import com.cevelop.constificator.visitors.MemberFunctionVisitor
import com.cevelop.constificator.visitors.MemberVariablesVisitor
import ch.hsr.ifs.iltis.cpp.core.ast.checker.SimpleChecker
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.composite.VisitorComposite
import com.cevelop.constificator.core.util.type.Truelean

class ConstificatorChecker : SimpleChecker<ProblemId>() {

    protected override fun createVisitor(): SimpleVisitor<ProblemId, Configuration>? {
        return VisitorComposite(
                LocalVariablesVisitor(this),
                FunctionParametersVisitor(this),
                MemberFunctionVisitor(this),
                MemberVariablesVisitor(this)
        )
    }

    protected override fun report() {
        val certain = Configuration.of(getProblemById(Markers.MissingQualification, getFile()))
        val uncertain = Configuration.of(getProblemById(Markers.PotentiallyMissingQualification, getFile()))
        nodesToReport.stream()
                .map { r -> r as DecisionReport }
                .filter { r -> this.shouldReport(r, certain, uncertain) }
                .filter { r -> r.getNode().getFileLocation() != null }
                .forEach { r -> reportProblem(r.getProblemId(), r.node) }
    }

    private fun shouldReport(report: DecisionReport, certain: Configuration, uncertain: Configuration): Boolean {
        when (report.decision.get()) {
            Truelean.YES -> {
                return (((report.decision is LocalVariableDecision && certain.checkLocalVariables) ||
                        (report.decision is FunctionParameterDecision && certain.checkFunctionParameters) ||
                        (report.decision is MemberFunctionDecision && certain.checkMemberFunctions) ||
                        (report.decision is MemberVariableDecision && certain.checkDataMembers)))
            }
            Truelean.MAYBE -> {
                return (((report.decision is LocalVariableDecision && uncertain.checkLocalVariables) ||
                        (report.decision is FunctionParameterDecision && uncertain.checkFunctionParameters) ||
                        (report.decision is MemberFunctionDecision && uncertain.checkMemberFunctions) ||
                        (report.decision is MemberVariableDecision && uncertain.checkDataMembers)))
            }
            else -> return false
        }
    }

    override fun initPreferences(problem: IProblemWorkingCopy) {
        super.initPreferences(problem)
        val iterator = Preferences.iterator()
        while (iterator.hasNext()) {
            val preference = iterator.next()
            addPreference(problem, preference.key, preference.description, preference.default)
        }
    }

}