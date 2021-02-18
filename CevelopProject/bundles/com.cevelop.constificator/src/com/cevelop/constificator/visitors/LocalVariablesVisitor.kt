package com.cevelop.constificator.visitors

import org.eclipse.cdt.core.dom.ast.IASTDeclarator
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable
import com.cevelop.constificator.checkers.Configuration
import com.cevelop.constificator.checkers.ProblemId
import com.cevelop.constificator.core.deciders.decision.LocalVariableDecision
import com.cevelop.constificator.core.deciders.localvariables.NonPointerVariable
import com.cevelop.constificator.core.deciders.localvariables.PointerVariable
import com.cevelop.constificator.core.util.ast.ASTRewriteCache
import com.cevelop.constificator.core.util.semantic.isPointer
import com.cevelop.constificator.core.util.semantic.isReference
import com.cevelop.constificator.core.util.type.Truelean
import com.cevelop.constificator.core.util.type.enumSetOf
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor

class LocalVariablesVisitor(reporter: ISimpleReporter<ProblemId>) : SimpleVisitor<ProblemId, Configuration>(reporter) {
    init {
        shouldVisitDeclarators = true
    }

    override fun getProblemIds() = enumSetOf<ProblemId>()

    override fun visit(declarator: IASTDeclarator?): Int {
        if (declarator !is ICPPASTDeclarator) {
            return PROCESS_SKIP
        }

        declarator.name?.nodeLocations?.let {
            if (it.size == 1 && it[0] is IASTMacroExpansionLocation) {
                return PROCESS_SKIP
            }
        }

        declarator.parent.let { parent ->
            if (parent !is IASTSimpleDeclaration || parent.declarators.size > 1) {
                return PROCESS_CONTINUE
            }

            parent.declSpecifier.let { declarationSpecifier ->
                if (declarationSpecifier is ICPPASTSimpleDeclSpecifier && declarationSpecifier.type == IASTSimpleDeclSpecifier.t_decltype_auto) {
                    return PROCESS_CONTINUE
                }
            }
        }

        (declarator.name.resolveBinding() as? ICPPVariable)?.let { variable ->
            if (variable is ICPPField) {
                return PROCESS_CONTINUE;
            }

            ASTRewriteCache(declarator.translationUnit.index).let { cache ->
                if (variable.type.isPointer || variable.type.isReference) {
                    PointerVariable.canConstify(declarator, cache, LocalVariableDecision::class.java)
                            .filter { it.get() !== Truelean.NO }
                            .map(::DecisionReport)
                            .forEach(reporter::addNodeForReporting)
                } else {
                    with(NonPointerVariable.canConstify(declarator, cache, LocalVariableDecision::class.java)) {
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