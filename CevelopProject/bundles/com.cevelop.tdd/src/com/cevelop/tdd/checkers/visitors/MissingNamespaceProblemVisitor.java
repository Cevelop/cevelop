package com.cevelop.tdd.checkers.visitors;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.infos.NamespaceInfo;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class MissingNamespaceProblemVisitor extends AbstractResolutionProblemVisitor {

    private Consumer3<IProblemId<ProblemId>, IASTName, NamespaceInfo> problemReporter;

    public MissingNamespaceProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, NamespaceInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    protected void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name) {
        IASTName partnameToReport = findFirstUnresolvableQualifier(name);
        if (partnameToReport != null && !(partnameToReport instanceof ICPPASTTemplateId)) {
            reportNamespaceProblem(partnameToReport, partnameToReport);
        }
    }

    // TODO(tstauber - Sep 18, 2018) Dafuq? Why does this take the same argument twice?
    private void reportNamespaceProblem(IASTName missingName, IASTName partnameToReport) {
        NamespaceInfo info = new NamespaceInfo();
        info.name = String.valueOf(missingName.getSimpleID());
        info.message = String.valueOf(missingName.getSimpleID());
        problemReporter.accept(ProblemId.MISSING_NAMESPACE, partnameToReport, info);
    }

    private IASTName findFirstUnresolvableQualifier(IASTName name) {
        if (name instanceof ICPPASTQualifiedName) {
            for (ICPPASTNameSpecifier partname : ((ICPPASTQualifiedName) name).getQualifier()) {
                if (partname instanceof IASTName) {
                    IBinding b = partname.resolveBinding();
                    if (b instanceof ICPPClassType) return null;
                    if (b instanceof IProblemBinding) return (IASTName) partname;
                }
            }
        }
        return null;
    }
}
