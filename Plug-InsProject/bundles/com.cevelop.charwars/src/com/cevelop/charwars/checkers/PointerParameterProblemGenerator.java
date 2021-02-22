package com.cevelop.charwars.checkers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.core.resources.IFile;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.info.CharwarsInfo;
import com.cevelop.charwars.utils.analyzers.DeclaratorTypeAnalyzer;


public class PointerParameterProblemGenerator {

    public static List<ProblemReport> generate(IFile file, IASTParameterDeclaration parameterDeclaration) {
        List<ProblemReport> problemReports = new ArrayList<>();
        IASTDeclarator declarator = parameterDeclaration.getDeclarator();
        if (DeclaratorTypeAnalyzer.isPointer(declarator) && !DeclaratorTypeAnalyzer.isArray(declarator) && ASTAnalyzer
                .isFunctionDefinitionParameterDeclaration(parameterDeclaration) && !DeclaratorTypeAnalyzer.hasCStringType(declarator, false) &&
            !DeclaratorTypeAnalyzer.hasCStringType(declarator, true)) {
            IASTName name = declarator.getName();

            ProblemReport report = ProblemReport.create(file, ProblemId.POINTER_PARAMETER_PROBLEM, name, new CharwarsInfo().also(i -> i.nodeName =
                                                                                                                                                 name.toString()));
            if (report != null) {
                problemReports.add(report);
            }
        }
        return problemReports;
    }

}
