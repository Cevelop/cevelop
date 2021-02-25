package com.cevelop.tdd.checkers.visitors;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.FreeFunctionInfo;


public class MissingFreeFunctionProblemVisitor extends AbstractResolutionProblemVisitor {

    private Consumer3<IProblemId<ProblemId>, IASTName, FreeFunctionInfo> problemReporter;

    public MissingFreeFunctionProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, FreeFunctionInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    protected void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name) {
        if (TddHelper.isFunctionCall(name.getParent())) {
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

    private void handleFunctionResolutionProblem(IASTName name, IProblemBinding problemBinding) {
        if (problemBinding.getCandidateBindings().length == 0 && !(name instanceof ICPPASTQualifiedName)) {
            reportMissingFunction(name, name);
        }
    }

    private void reportMissingFunction(IASTName missingName, IASTName name) {
        FreeFunctionInfo info = new FreeFunctionInfo();
        info.functionName = new String(missingName.getSimpleID());
        info.message = new String(missingName.getSimpleID());
        problemReporter.accept(ProblemId.MISSING_FREE_FUNCTION, name.getLastName(), info);
    }

}
