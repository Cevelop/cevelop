package com.cevelop.tdd.checkers.visitors;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.TypeInfo;


public class MissingTypeProblemVisitor extends AbstractResolutionProblemVisitor {

    private Consumer3<IProblemId<ProblemId>, IASTName, TypeInfo> problemReporter;

    public MissingTypeProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, TypeInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    private static final String TEMPLATE_ARGUMENT_SEPARATOR = ",";
    ArrayList<IASTNode>         reportedNames               = new ArrayList<>();

    @Override
    protected void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name) {
        // do not report B<int> twice
        if (name instanceof ICPPASTQualifiedName) {
            return;
        }
        if (name.getParent() != null && name.getParent() instanceof ICPPASTTemplateId && reportedNames.contains(name.getParent())) {
            return;
        }

        final ICPPASTNamedTypeSpecifier nts = TddHelper.getAncestorOfType(name, CPPASTNamedTypeSpecifier.class);
        if (nts != null && nts.isTypename()) {
            return;
        }
        TypeInfo info = createInfo(name, String.valueOf(name.getSimpleID()), nts);
        if (TddHelper.nameNotFoundProblem(problemBinding)) {
            int roleOfName = name.getRoleOfName(true);
            if (roleOfName != IASTNameOwner.r_declaration && roleOfName != IASTNameOwner.r_definition) {
                reportType(name, info);
            }
            //         if (name instanceof ICPPASTQualifiedName) {
            //            MutableList<ICPPASTNameSpecifier> qualifier = Lists.mutable.of(((ICPPASTQualifiedName) name).getQualifier());
            //            qualifier.add((ICPPASTNameSpecifier) name.getLastName());
            //
            //            for (ICPPASTNameSpecifier s : qualifier) {
            //               if (s instanceof IASTName) {
            //                  IBinding binding = s.resolveBinding();
            //                  if (binding instanceof IProblemBinding && TddHelper.nameNotFoundProblem((IProblemBinding) binding)) ;
            //               }
            //            }
            //
            //         } else {
            //            if (name.getPropertyInParent() != ICPPASTQualifiedName.SEGMENT_NAME) reportType(name, info);
            //         }
        } else if (TddHelper.isInvalidType(problemBinding)) {
            reportType(name, info);
        }
    }

    private TypeInfo createInfo(IASTName name, final String missingName, final ICPPASTNamedTypeSpecifier nts) {
        ICPPASTTemplateId templateId = extractTemplateId(name, nts);
        TypeInfo info = new TypeInfo();
        info.typeName = missingName;
        info.message = missingName;
        if (templateId != null) {
            info.templateArgumentsString = composeArgumentString(templateId);
        }
        return info;
    }

    private void reportType(IASTName name, TypeInfo info) {
        reportedNames.add(name);
        problemReporter.accept(ProblemId.MISSING_TYPE, name, info);
    }

    private String composeArgumentString(ICPPASTTemplateId templateId) {
        String args = "";
        for (IASTNode node : templateId.getTemplateArguments()) {
            if (node instanceof CPPASTTypeId) {
                IASTDeclSpecifier declspec = ((CPPASTTypeId) node).getDeclSpecifier();
                args += declspec.getRawSignature() + TEMPLATE_ARGUMENT_SEPARATOR;
            }
        }
        if (!args.isEmpty()) {
            args = args.substring(0, args.length() - TEMPLATE_ARGUMENT_SEPARATOR.length());
        }
        return args;
    }

    private ICPPASTTemplateId extractTemplateId(IASTName name, final ICPPASTNamedTypeSpecifier nts) {
        ICPPASTTemplateId templid = null;
        if (nts != null && nts.getName() instanceof ICPPASTTemplateId) {
            templid = (ICPPASTTemplateId) nts.getName();
        } else if (name instanceof ICPPASTTemplateId) {
            templid = (ICPPASTTemplateId) name;
        }
        return templid;
    }
}
