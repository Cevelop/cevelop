package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression.CaptureDefault;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;

import com.cevelop.codeanalysator.autosar.util.AliasFinder;
import com.cevelop.codeanalysator.autosar.util.LambdaHelper;
import com.cevelop.codeanalysator.autosar.util.ScopeHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotOutliveReferenceCapturedObjectsVisitor extends CodeAnalysatorVisitor {

    public DoNotOutliveReferenceCapturedObjectsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof ICPPASTLambdaExpression) {
            ICPPASTLambdaExpression lambdaExpression = (ICPPASTLambdaExpression) expression;
            new LambdaLifetimeChecker(lambdaExpression).check();
        }
        return super.visit(expression);
    }

    private class LambdaLifetimeChecker {

        private final ICPPASTLambdaExpression lambdaExpression;
        private final IScope                  outermostValidScope;
        private final AliasFinder             aliasFinder;

        private LambdaLifetimeChecker(ICPPASTLambdaExpression lambdaExpression) {
            this.lambdaExpression = lambdaExpression;
            this.outermostValidScope = getOutermostValidScope(lambdaExpression);
            aliasFinder = new AliasFinder(lambdaExpression.getTranslationUnit());
        }

        private IScope getOutermostValidScope(ICPPASTLambdaExpression lambdaExpression) {
            Collection<IVariable> referenceCapturedVariables = getReferenceCapturedVariables(lambdaExpression);
            return referenceCapturedVariables.stream() //
                    .filter(Objects::nonNull) //
                    .map(ScopeHelper::getBindingScope) //
                    .min(this::compareNestedScopes) //
                    .map(scope -> limitScopeToOutermostValidLambdaScope(scope, lambdaExpression, referenceCapturedVariables)) //
                    .orElse(null);
        }

        private Set<IVariable> getReferenceCapturedVariables(ICPPASTLambdaExpression lambdaExpression) {
            Set<IVariable> referenceCapturedVariables = getExplicitlyReferenceCapturedVariables(lambdaExpression);
            if (lambdaExpression.getCaptureDefault() == CaptureDefault.BY_REFERENCE) {
                referenceCapturedVariables.addAll(LambdaHelper.getImplicitlyCapturedVariables(lambdaExpression));
            }
            return referenceCapturedVariables;
        }

        private Set<IVariable> getExplicitlyReferenceCapturedVariables(ICPPASTLambdaExpression lambdaExpression) {
            return Arrays.stream(lambdaExpression.getCaptures()) //
                    .filter(ICPPASTCapture::isByReference) //
                    .map(ICPPASTCapture::getIdentifier) //
                    .filter(Objects::nonNull) //
                    .map(IASTName::resolveBinding) //
                    .filter(IVariable.class::isInstance) //
                    .map(IVariable.class::cast) //
                    .collect(Collectors.toCollection(HashSet::new));
        }

        private int compareNestedScopes(IScope first, IScope second) {
            if (first == second) {
                return 0;
            }
            if (ScopeHelper.isSameOrChildScope(first, second)) {
                return -1;
            } else {
                return 1;
            }
        }

        private IScope limitScopeToOutermostValidLambdaScope(IScope outermostValidScope, ICPPASTLambdaExpression lambdaExpression,
                Collection<IVariable> referenceCapturedVariables) {
            for (ICPPASTLambdaExpression outerLambdaExpression = LambdaHelper.getEnclosingLambda(lambdaExpression); //
                    outerLambdaExpression != null; outerLambdaExpression = LambdaHelper.getEnclosingLambda(outerLambdaExpression)) {
                IScope outerLambdaScope = ScopeHelper.getLambdaScope(outerLambdaExpression);
                if (!ScopeHelper.isSameOrChildScope(outerLambdaScope, outermostValidScope)) {
                    break;
                }

                if (!doesLambdaReferenceCaptureAllVariables(outerLambdaExpression, referenceCapturedVariables)) {
                    return outerLambdaScope;
                }
            }
            return outermostValidScope;
        }

        private boolean doesLambdaReferenceCaptureAllVariables(ICPPASTLambdaExpression lambda, Collection<IVariable> variables) {
            return getReferenceCapturedVariables(lambda).containsAll(variables);
        }

        private void check() {
            if (hasLimitedValidScope()) {
                checkExpression(lambdaExpression);
            }
        }

        private boolean hasLimitedValidScope() {
            return outermostValidScope != null;
        }

        private void checkExpression(IASTExpression expression) {
            IASTNode parent = expression.getParent();
            if (parent instanceof IASTReturnStatement) {
                IASTReturnStatement returnStatement = (IASTReturnStatement) parent;
                checkReturnStatement(returnStatement);
            } else {
                aliasFinder.findAlias(expression) //
                        .ifPresent(variable -> checkVariable(variable, parent));
            }
        }

        private void checkReturnStatement(IASTReturnStatement returnStatement) {
            if (!isTargetInsideScope(returnStatement)) {
                reportRuleForNode(returnStatement);
            }
        }

        private boolean isTargetInsideScope(IASTReturnStatement returnStatement) {
            for (IASTNode parent = returnStatement.getParent(); parent != null; parent = parent.getParent()) {
                if (parent instanceof ICPPASTFunctionDefinition) {
                    break;
                }

                if (parent instanceof ICPPASTLambdaExpression) {
                    ICPPASTLambdaExpression outerLambdaExpression = (ICPPASTLambdaExpression) parent;
                    IScope lambdaScope = ScopeHelper.getLambdaScope(outerLambdaExpression);
                    if (lambdaScope != null) {
                        IScope lambdaParentScope = ScopeHelper.getParentScope(lambdaScope);
                        return ScopeHelper.isSameOrChildScope(lambdaParentScope, outermostValidScope);
                    }
                }
            }
            return false;
        }

        private void checkVariable(IVariable variable, IASTNode nodeToReport) {
            if (variable instanceof ICPPField) {
                reportRuleForNode(nodeToReport);
            } else if (variable instanceof ICPPParameter) {
                ICPPParameter parameter = (ICPPParameter) variable;
                if (parameter.getType() instanceof ICPPReferenceType) {
                    reportRuleForNode(nodeToReport);
                } else {
                    checkLocalVariable(variable, nodeToReport);
                }
            } else {
                IBinding variableOwner = variable.getOwner();
                if (variableOwner == null || variableOwner instanceof ICPPNamespace) {
                    reportRuleForNode(nodeToReport);
                } else if (variableOwner instanceof ICPPFunction || variableOwner instanceof ICPPClassType) {
                    if (variable.isStatic()) {
                        reportRuleForNode(nodeToReport);
                    } else {
                        checkLocalVariable(variable, nodeToReport);
                    }
                }
            }
        }

        private void checkLocalVariable(IVariable variable, IASTNode nodeToReport) {
            IScope variableScope = ScopeHelper.getBindingScope(variable);
            if (!ScopeHelper.isSameOrChildScope(variableScope, outermostValidScope)) {
                reportRuleForNode(nodeToReport);
            } else {
                aliasFinder.getUsesOfAlias(variable) //
                        .forEach(aliasUseExpression -> checkExpression(aliasUseExpression));
            }
        }
    }
}
