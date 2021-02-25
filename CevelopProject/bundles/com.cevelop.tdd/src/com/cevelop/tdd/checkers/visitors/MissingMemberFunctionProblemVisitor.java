package com.cevelop.tdd.checkers.visitors;

import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.ALLCVQ;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.PTR;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.REF;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.TDEF;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.MemberFunctionInfo;


public class MissingMemberFunctionProblemVisitor extends AbstractResolutionProblemVisitor {

    private Consumer3<IProblemId<ProblemId>, IASTName, MemberFunctionInfo> problemReporter;

    public MissingMemberFunctionProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, MemberFunctionInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    protected void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name) {
        if (TddHelper.nameNotFoundProblem(problemBinding) && isFieldReference(name)) {
            handleMemberResolutionProblem(name, problemBinding);
        } else if (TddHelper.isFunctionCall(name.getParent())) {
            handleFunctionResolutionProblem(name, problemBinding);
        }
    }

    @Override
    public int visit(IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            return PROCESS_SKIP;
        } else {
            return super.visit(name);
        }
    }

    private boolean isFieldReference(IASTName name) {
        return name.getParent() instanceof ICPPASTFieldReference;
    }

    private void handleFunctionResolutionProblem(IASTName name, IProblemBinding problemBinding) {
        if (problemBinding.getCandidateBindings().length == 0 && name instanceof ICPPASTQualifiedName) {
            handleQualifiedName(name, (CPPASTQualifiedName) name);
        }
    }

    private void handleQualifiedName(IASTName name, CPPASTQualifiedName qname) {
        IASTName lastName = qname.getLastName();
        ICPPASTNameSpecifier[] segments = qname.getAllSegments();
        boolean isTypeMember = (segments.length > 1) && segments[segments.length - 2].resolveBinding() instanceof ICPPClassType;
        if (!(lastName instanceof ICPPASTTemplateId) && isTypeMember) {
            reportMissingStaticMember(name, lastName);
        } else {
            reportMissingNamespaceFunction(name, lastName);
        }
    }

    private void reportMissingNamespaceFunction(IASTName missingName, IASTName name) {
        MemberFunctionInfo info = new MemberFunctionInfo();
        info.name = new String(missingName.getSimpleID());
        info.message = new String(missingName.getSimpleID());
        problemReporter.accept(ProblemId.MISSING_MEMBER_FUNCTION, name.getLastName(), info);
    }

    private void reportMissingStaticMember(IASTName missingName, IASTName name) {
        MemberFunctionInfo info = new MemberFunctionInfo();
        info.name = new String(missingName.getSimpleID());
        info.message = new String(missingName.getSimpleID());
        info.mustBeStatic = true;
        problemReporter.accept(ProblemId.MISSING_MEMBER_FUNCTION, name, info);
    }

    private void reportMemberProblem(IASTName missingName, IASTName name) {
        MemberFunctionInfo info = new MemberFunctionInfo();
        info.name = new String(missingName.getSimpleID());
        info.message = new String(missingName.getSimpleID());
        problemReporter.accept(ProblemId.MISSING_MEMBER_FUNCTION, name.getLastName(), info);
    }

    private void handleMemberResolutionProblem(IASTName name, IProblemBinding problemBinding) {
        if (TddHelper.isMethod(name)) {
            IASTFieldReference member = TddHelper.getAncestorOfType(name, IASTFieldReference.class);
            IASTExpression expression = member.getFieldOwner();
            if (!isOfTypeWithMembers(expression)) {
                return;
            }
            if (problemBinding.getCandidateBindings().length == 0) {
                reportMemberProblem(name, name);
            }
        }
    }

    private boolean isOfTypeWithMembers(IASTExpression expression) {
        IType expressionType = SemanticUtil.getNestedType(expression.getExpressionType(), ALLCVQ | TDEF | REF | PTR);
        return expressionType instanceof ICPPClassType;
    }
}
