package com.cevelop.constificator.visitors

import org.eclipse.cdt.core.dom.ast.IASTDeclarator
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField
import com.cevelop.constificator.checkers.Configuration
import com.cevelop.constificator.checkers.ProblemId
import com.cevelop.constificator.core.deciders.classmembers.MemberVariable
import com.cevelop.constificator.core.deciders.decision.MemberVariableDecision
import com.cevelop.constificator.core.deciders.localvariables.NonPointerVariable
import com.cevelop.constificator.core.deciders.localvariables.PointerVariable
import com.cevelop.constificator.core.util.ast.ASTRewriteCache
import com.cevelop.constificator.core.util.semantic.isPointer
import com.cevelop.constificator.core.util.semantic.isReference
import com.cevelop.constificator.core.util.structural.descendsFrom
import com.cevelop.constificator.core.util.type.Truelean
import com.cevelop.constificator.core.util.type.enumSetOf
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor

class MemberVariablesVisitor(reporter: ISimpleReporter<ProblemId>?) : SimpleVisitor<ProblemId, Configuration>(reporter) {
    init {
        shouldVisitDeclarators = true
    }

    override fun getProblemIds() = enumSetOf<ProblemId>()

    override fun visit(declarator: IASTDeclarator?): Int {
        if (declarator !is ICPPASTDeclarator || declarator is ICPPASTFunctionDeclarator) {
            return PROCESS_SKIP
        }

        if (declarator.descendsFrom<ICPPASTTemplateDeclaration>()) {
            return PROCESS_SKIP
        }

        declarator.name?.nodeLocations?.let {
            if (it.size == 1 && it[0] is IASTMacroExpansionLocation) {
                return PROCESS_SKIP
            }
        }

        declarator.parent.let {
            if (it !is IASTSimpleDeclaration || it.declarators.size > 1) {
                return PROCESS_CONTINUE
            }
        }

        (declarator.name.resolveBinding() as? ICPPField)?.let { field ->
            ASTRewriteCache(declarator.translationUnit.index).let { cache ->
                if (MemberVariable.canConstify(declarator, cache).get() === Truelean.NO) {
                    return PROCESS_CONTINUE
                } else if (field.type.isPointer || field.type.isReference) {
                    PointerVariable.canConstify(declarator, cache, MemberVariableDecision::class.java)
                            .filter { it.get() !== Truelean.NO }
                            .map(::DecisionReport)
                            .forEach(reporter::addNodeForReporting)
                } else {
                    with(NonPointerVariable.canConstify(declarator, cache, MemberVariableDecision::class.java)) {
                        if (get() !== Truelean.NO) {
                            reporter.addNodeForReporting(DecisionReport(this))
                        }
                    }

                }
            }
        }

        return PROCESS_CONTINUE
    }
}