package com.cevelop.constificator.visitors

import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration
import org.eclipse.cdt.core.dom.ast.IASTPointer
import org.eclipse.cdt.core.dom.ast.ITypedef
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil
import com.cevelop.constificator.checkers.Configuration
import com.cevelop.constificator.checkers.ProblemId
import com.cevelop.constificator.core.deciders.decision.FunctionParameterDecision
import com.cevelop.constificator.core.deciders.functionparameters.ArrayParameter
import com.cevelop.constificator.core.deciders.functionparameters.NonPointerParameter
import com.cevelop.constificator.core.deciders.functionparameters.PointerParameter
import com.cevelop.constificator.core.util.ast.ASTRewriteCache
import com.cevelop.constificator.core.util.semantic.isArrayLike
import com.cevelop.constificator.core.util.semantic.isConst
import com.cevelop.constificator.core.util.semantic.isMoveAssignmentOperator
import com.cevelop.constificator.core.util.semantic.isMoveContructor
import com.cevelop.constificator.core.util.semantic.isPointer
import com.cevelop.constificator.core.util.semantic.isReference
import com.cevelop.constificator.core.util.structural.descendsFrom
import com.cevelop.constificator.core.util.structural.hasTypedGrandparent
import com.cevelop.constificator.core.util.type.Truelean
import com.cevelop.constificator.core.util.type.enumSetOf
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor

@SuppressWarnings("restriction")
class FunctionParametersVisitor(reporter: ISimpleReporter<ProblemId>?) : SimpleVisitor<ProblemId, Configuration>(reporter) {
    init {
        shouldVisitParameterDeclarations = true
    }

    override fun getProblemIds() = enumSetOf<ProblemId>()

    override fun visit(parameterDeclaration: IASTParameterDeclaration?): Int {
        if (parameterDeclaration !is ICPPASTParameterDeclaration) {
            return PROCESS_SKIP
        }

        if (!(parameterDeclaration.hasTypedGrandparent<ICPPASTFunctionDefinition>() ||
              parameterDeclaration.hasTypedGrandparent<ICPPASTLambdaExpression>())) {
            return PROCESS_SKIP
        }

        (parameterDeclaration.parent as? ICPPASTFunctionDeclarator)?.let {
            if (it.descendsFrom<ICPPASTTemplateDeclaration>()) {
                return PROCESS_SKIP
            }

            if (it.name?.toString() == "main") {
                return PROCESS_SKIP
            }

            (it.name?.resolveBinding() as? ICPPMethod)?.let { memberFunction ->
                if (memberFunction.isMoveContructor || memberFunction.isMoveAssignmentOperator) {
                    return PROCESS_SKIP
                }
            }
        }

        parameterDeclaration.declarator.let { parameterDeclarator ->
            if (parameterDeclarator.name.toString().isEmpty() && (parameterDeclarator.nestedDeclarator?.name?.toString()?.isEmpty()
                            ?: true)) {
                return PROCESS_SKIP
            }

            val parameter = parameterDeclarator.name.resolveBinding() as ICPPParameter
            val type = SemanticUtil.getSimplifiedType(parameter.type)
			
            val aliasedParameterType = SemanticUtil.getSimplifiedType((parameterDeclaration.declSpecifier as? ICPPASTNamedTypeSpecifier)?.let {
                it.name.resolveBinding() as? ITypedef
            })

            // FIXME: This is part 1 of a workaround for a CDT bug causing top-level cv-qualification of lambda parameters getting lost
            if (parameterDeclaration.hasTypedGrandparent<ICPPASTLambdaExpression>()) {
                if (!type.isPointer && !type.isReference) {
                    if (parameterDeclaration.declSpecifier.isConst || aliasedParameterType?.isConst(0) ?: false) {
                        return PROCESS_CONTINUE
                    }
                }
            }

            ASTRewriteCache(parameterDeclarator.translationUnit.index).let { cache ->
                if (type.isArrayLike || parameterDeclarator is ICPPASTArrayDeclarator || aliasedParameterType is IArrayType) {
                    with(ArrayParameter.canConstify(parameterDeclarator, cache)) {
                        if (get() !== Truelean.NO) {
                            reporter.addNodeForReporting(DecisionReport(this))
                        }
                    }
                } else if (type.isPointer || type.isReference) {
                    PointerParameter.canConstify(parameterDeclarator, cache)
                            .filter { it.get() !== Truelean.NO }
                            // FIXME: This is part 2 of the workaround mentioned above
                            .filter {
                                if (it is FunctionParameterDecision) {
                                    !((it.node() as? IASTPointer)?.isConst ?: false)
                                } else {
                                    true
                                }
                            }
                            .map(::DecisionReport)
                            .forEach(reporter::addNodeForReporting)
                } else {
                    with(NonPointerParameter.canConstify(parameterDeclarator, cache)) {
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