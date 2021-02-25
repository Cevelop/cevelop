package com.cevelop.charwars.checkers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.codan.core.cxx.CxxAstUtils;
import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.FunctionBindingAnalyzer;
import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.info.CharwarsInfo;
import com.cevelop.charwars.utils.ErrorLogger;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;


public class CStrProblemGenerator {

    public static List<ProblemReport> generate(IFile file, IASTExpression expression) {
        List<ProblemReport> problemReports = new ArrayList<>();
        if (ASTAnalyzer.isConversionToCharPointer(expression, true)) {
            IASTFunctionCallExpression cStrCall = (IASTFunctionCallExpression) expression;
            IASTNode parent = cStrCall.getParent();
            IASTName name = null;
            int strArgIndex = -1;

            if (parent instanceof ICPPASTBinaryExpression) {
                ICPPASTBinaryExpression binaryExpression = (ICPPASTBinaryExpression) parent;
                IASTImplicitName implicitNames[] = binaryExpression.getImplicitNames();
                if (implicitNames.length == 0) {
                    return problemReports;
                }
                name = implicitNames[0];
                strArgIndex = BEAnalyzer.isOp1(cStrCall) ? 0 : 1;
            } else {
                name = FunctionBindingAnalyzer.getFunctionName(parent);
                strArgIndex = FunctionBindingAnalyzer.getArgIndex(parent, cStrCall);
            }

            if (name != null) {
                OverloadChecker overloadChecker = new OverloadChecker();
                ICPPFunction[] validOverloads = overloadChecker.getValidOverloads(name, strArgIndex);
                if (validOverloads.length > 0) {
                    ICPPFunction firstValidOverload = validOverloads[0];
                    String signature = findSignature(firstValidOverload, parent.getTranslationUnit());
                    final String nodeName = signature == null ? buildSignature(firstValidOverload) : signature;

                    ProblemReport report = ProblemReport.create(file, ProblemId.C_STR_PROBLEM, cStrCall, new CharwarsInfo().also(i -> i.nodeName =
                                                                                                                                                 nodeName));
                    if (report != null) {
                        problemReports.add(report);
                    }
                }
            }
        }
        return problemReports;
    }

    private static String findSignature(ICPPBinding binding, IASTTranslationUnit astTranslationUnit) {
        try {
            IIndex index = astTranslationUnit.getIndex();
            IIndexBinding adaptedBinding = index.adaptBinding(binding);
            IIndexName[] indexNames = index.findNames(adaptedBinding, IIndex.FIND_DECLARATIONS_DEFINITIONS);
            for (IIndexName indexName : indexNames) {
                ITranslationUnit translationUnit = CxxAstUtils.getTranslationUnitFromIndexName(indexName);
                if (translationUnit == null) {
                    continue;
                }

                IASTTranslationUnit atu = translationUnit.getAST(index, ITranslationUnit.AST_SKIP_ALL_HEADERS |
                                                                        ITranslationUnit.AST_SKIP_FUNCTION_BODIES);
                IASTName declarationName = (IASTName) ASTAnalyzer.getMarkedNode(atu, indexName.getNodeOffset(), indexName.getNodeLength());
                IASTNode parent = declarationName.getParent().getParent();
                if (parent instanceof ICPPASTFunctionDefinition) {
                    ICPPASTFunctionDefinition funcDefinition = (ICPPASTFunctionDefinition) parent.copy();
                    funcDefinition.setBody(null);
                    return ASTAnalyzer.nodeToString(funcDefinition).trim() + ";";
                } else if (parent instanceof IASTSimpleDeclaration) {
                    return ASTAnalyzer.nodeToString(parent);
                }
            }
        } catch (Exception e) {
            ErrorLogger.log("Unable to detect function signature. Using fall-back solution instead.", e);
        }
        return null;
    }

    private static String buildSignature(ICPPFunction function) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ASTTypeUtil.getType(function.getType().getReturnType(), false));
        buffer.append(" ");
        buffer.append(function.getName());
        buffer.append("(");

        ICPPParameter parameters[] = function.getParameters();
        for (ICPPParameter parameter : parameters) {
            buffer.append(ASTTypeUtil.getType(parameter.getType()));
            buffer.append(" ");
            buffer.append(parameter.getName());

            if (parameter != parameters[parameters.length - 1]) {
                buffer.append(", ");
            }
        }

        buffer.append(")");
        return buffer.toString();
    }
}
