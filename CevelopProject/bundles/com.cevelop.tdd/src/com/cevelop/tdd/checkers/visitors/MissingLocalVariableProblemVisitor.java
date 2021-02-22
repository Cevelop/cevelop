package com.cevelop.tdd.checkers.visitors;

import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.ALLCVQ;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.PTR;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.REF;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.TDEF;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.LocalVariableInfo;
import com.cevelop.tdd.infos.MemberVariableInfo;


public class MissingLocalVariableProblemVisitor extends AbstractResolutionProblemVisitor {

    private final boolean                                                   checkMemberVariable;
    private final Consumer3<IProblemId<ProblemId>, IASTName, MarkerInfo<?>> problemReporter;

    public MissingLocalVariableProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, MarkerInfo<?>> problemReporter,
                                              boolean checkMemberVariable) {
        this.problemReporter = problemReporter;
        this.checkMemberVariable = checkMemberVariable;
    }

    @Override
    public int visit(IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            return PROCESS_SKIP;
        }
        return super.visit(name);
    }

    @Override
    protected void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name) {
        if (TddHelper.nameNotFoundProblem(problemBinding)) {
            if (name instanceof ICPPASTQualifiedName) {
                return;
            }
            if (isIdentifyingFunctionCallPart(name)) {
                return;
            }
            if (TddHelper.isInvalidType(problemBinding)) {
                return;
            }
            if (!TddHelper.isLastPartOfQualifiedName(name)) {
                return;
            }
            if (TddHelper.hasUnresolvableNameQualifier(name)) {
                return;
            }
            IASTNode upmostName = TddHelper.getLastOfSameAncestor(name, IASTName.class);
            IASTNode parent = upmostName.getParent();
            if (parent instanceof IASTNamedTypeSpecifier) {
                return;
            }
            if (parent instanceof ICPPASTBaseSpecifier) {
                return;
            }
            if (upmostName instanceof ICPPASTQualifiedName) {
                handleStaticMemberVariable(name);
                return;
            }
            if (parent instanceof CPPASTTemplateId) {
                CPPASTTemplateId templid = (CPPASTTemplateId) name.getParent();
                IBinding b = templid.getBinding();
                if (b instanceof IProblemBinding && TddHelper.isInvalidType((IProblemBinding) b)) {
                    return;
                }
            }
            if (TddHelper.isFunctionCall(parent)) {
                return;
            }
            if (problemBinding.getCandidateBindings().length > 0) {
                return;
            }
            String missingName = name.getBinding().getName();
            boolean parentIsCtorChainInit = parent instanceof ICPPASTConstructorChainInitializer;
            boolean parentIsFieldRef = parent instanceof IASTFieldReference;

            if (checkMemberVariable) {
                if (parentIsCtorChainInit) {
                    reportMissingMemberVariable(name, missingName);
                } else if (parentIsFieldRef) {
                    if (TddHelper.getAncestorOfType(name, IASTFunctionCallExpression.class) != null) {
                        return;
                    }
                    if (!isOfTypeWithMembers(((IASTFieldReference) parent).getFieldOwner())) {
                        return;
                    }
                    reportMissingMemberVariable(name, missingName);
                }
            } else if (!(parentIsCtorChainInit || parentIsFieldRef)) {
                LocalVariableInfo info = new LocalVariableInfo();
                info.name = missingName;
                info.message = missingName;
                problemReporter.accept(ProblemId.MISSING_LOCAL_VARIABLE, name, info);
            }
        }
    }

    private boolean isIdentifyingFunctionCallPart(IASTName name) {
        return (name.getPropertyInParent() == IASTFieldReference.FIELD_NAME || (name.getParent() instanceof ICPPASTQualifiedName && TddHelper
                .isLastPartOfQualifiedName(name))) && TddHelper.getAncestorOfType(name, IASTFunctionCallExpression.class) != null;
    }

    private boolean isOfTypeWithMembers(IASTExpression expression) {
        IType expressionType = SemanticUtil.getNestedType(expression.getExpressionType(), ALLCVQ | TDEF | REF | PTR);
        return expressionType instanceof ICPPClassType;
    }

    private void reportMissingMemberVariable(IASTName name, String missingName) {
        MemberVariableInfo info = new MemberVariableInfo();
        info.name = missingName;
        info.message = missingName;
        problemReporter.accept(ProblemId.MISSING_MEMBER_VARIABLE, name, info);
    }

    private void handleStaticMemberVariable(IASTName name) {
        reportMissingMemberVariable(name, name.resolveBinding().getName());
    }

}
