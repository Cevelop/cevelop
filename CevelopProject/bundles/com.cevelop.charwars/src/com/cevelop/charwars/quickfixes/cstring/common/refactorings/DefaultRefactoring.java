package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IType;

import com.cevelop.charwars.asttools.FunctionBindingAnalyzer;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.analyzers.TypeAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class DefaultRefactoring extends Refactoring {

    private static final IBetterFactory FACTORY          = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
    private static final String         CONVERT_TO_CONST = "CONVERT_TO_CONST";

    public DefaultRefactoring(EnumSet<Kind> contextKinds) {
        setContextKinds(contextKinds);
    }

    private void makeApplicable(IASTNode nodeToReplace, boolean convertToConst) {
        super.makeApplicable(nodeToReplace);
        config.put(CONVERT_TO_CONST, convertToConst);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        IType parameterType = FunctionBindingAnalyzer.getParameterType(idExpression);
        boolean isConst = !TypeAnalyzer.isCStringType(parameterType, false);
        makeApplicable(idExpression, isConst);
    }

    @Override
    protected IASTNode getReplacementNode(IASTIdExpression idExpression, Context context) {
        IASTName stringName = context.createStringVarName();
        boolean convertToConst = (boolean) config.get(CONVERT_TO_CONST);

        if (convertToConst) {
            IASTFunctionCallExpression c_strCall = FACTORY.newMemberFunctionCallExpression(stringName, StdString.C_STR);
            if (context.isOffset(idExpression)) {
                IASTBinaryExpression plusExpression = FACTORY.newPlusExpression(c_strCall, context.createOffsetVarIdExpression());
                return FACTORY.newBracketedExpression(plusExpression);
            } else {
                return c_strCall;
            }
        } else {
            IASTFunctionCallExpression beginCall = FACTORY.newMemberFunctionCallExpression(stringName, StdString.BEGIN);
            IASTUnaryExpression dereferenceOperatorExpression = FACTORY.newDereferenceOperatorExpression(beginCall);
            return FACTORY.newAdressOperatorExpression(dereferenceOperatorExpression);
        }
    }
}
