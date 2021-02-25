package com.cevelop.charwars.checkers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.core.resources.IFile;

import com.cevelop.charwars.asttools.DeclaratorAnalyzer;
import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.info.CharwarsInfo;


public class CStringAliasProblemGenerator {

    public static List<ProblemReport> generate(IFile file, IASTNode node) {
        List<ProblemReport> problemReports = new ArrayList<>();
        if (node instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) node;
            for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
                if (DeclaratorAnalyzer.isCStringAlias(declarator)) {
                    IASTName name = declarator.getName();
                    ProblemReport report = ProblemReport.create(file, ProblemId.C_STRING_ALIAS_PROBLEM, name, new CharwarsInfo().also(
                            i -> i.nodeName = name.toString()));
                    if (report != null) {
                        problemReports.add(report);
                    }
                }
            }
        }
        return problemReports;
    }
}
