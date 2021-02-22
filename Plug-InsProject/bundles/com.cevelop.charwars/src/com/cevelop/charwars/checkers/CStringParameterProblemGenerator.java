package com.cevelop.charwars.checkers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.core.resources.IFile;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.FunctionBindingAnalyzer;
import com.cevelop.charwars.asttools.IndexFinder;
import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.info.CharwarsInfo;
import com.cevelop.charwars.utils.analyzers.DeclaratorTypeAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;


public class CStringParameterProblemGenerator {

    public static List<ProblemReport> generate(IFile file, IASTParameterDeclaration parameterDeclaration) {
        List<ProblemReport> problemReports = new ArrayList<>();
        IASTDeclarator declarator = parameterDeclaration.getDeclarator();
        if (ASTAnalyzer.isFunctionDefinitionParameterDeclaration(parameterDeclaration) && DeclaratorTypeAnalyzer.hasCStringType(declarator, true)) {
            if (!isStdStringOverloadAvailable(parameterDeclaration)) {
                IASTName name = declarator.getName();
                ProblemReport report = ProblemReport.create(file, ProblemId.C_STRING_PARAMETER_PROBLEM, name, new CharwarsInfo().also(
                        i -> i.nodeName = name.toString()));
                if (report != null) {
                    problemReports.add(report);
                }
            }
        }
        return problemReports;
    }

    private static boolean isStdStringOverloadAvailable(IASTParameterDeclaration cStrParameter) {
        try {
            int strParameterIndex = FunctionAnalyzer.getParameterIndex(cStrParameter);
            ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) cStrParameter.getParent();
            IASTName functionName = functionDeclarator.getName();

            IIndex index = functionName.getTranslationUnit().getIndex();
            ICPPFunction originalOverload = (ICPPFunction) index.adaptBinding(functionName.resolveBinding());
            IIndexBinding bindings[] = IndexFinder.findBindings(functionName);
            for (IIndexBinding binding : bindings) {
                if (binding instanceof ICPPFunction) {
                    ICPPFunction possibleOverload = (ICPPFunction) binding;
                    if (FunctionBindingAnalyzer.isValidOverload(originalOverload, possibleOverload, strParameterIndex)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
