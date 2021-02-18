package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IType;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.FunctionBindingAnalyzer;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;
import com.cevelop.charwars.utils.analyzers.TypeAnalyzer;


public class NullRefactoring extends Refactoring {

    public NullRefactoring(EnumSet<Kind> contextKinds) {
        setContextKinds(contextKinds);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        IASTNode parent = idExpression.getParent();
        boolean isNotOffset = !context.isOffset(idExpression);

        if (BEAnalyzer.isPlusAssignment(parent) || FunctionAnalyzer.isCallToMemberFunction(parent, Function.COMPARE) || FunctionAnalyzer
                .isCallToMemberFunction(parent, Function.FIND_FIRST_OF) || FunctionAnalyzer.isCallToMemberFunction(parent, Function.APPEND) ||
            FunctionAnalyzer.isCallToMemberFunction(parent, Function.REPLACE) || FunctionAnalyzer.isCallToMemberFunction(parent,
                    Function.FIND_FIRST_NOT_OF) || FunctionAnalyzer.isCallToMemberFunction(parent, Function.FIND) || (ASTAnalyzer
                            .isArraySubscriptExpression(idExpression) && isNotOffset) || ASTAnalyzer.isLValueInAssignment(idExpression) ||
            (ASTAnalyzer.isLeftShiftExpressionToStdCout(parent) && isNotOffset) || (isStdStringParameterDeclaration(idExpression, context) &&
                                                                                    isNotOffset)) {

            makeApplicable(null);
        }
    }

    private boolean isStdStringParameterDeclaration(IASTIdExpression idExpression, Context context) {
        IType parameterType = FunctionBindingAnalyzer.getParameterType(idExpression);
        if (parameterType == null)
            return false;
        else return TypeAnalyzer.isStdStringType(parameterType);
    }

    @Override
    protected void updateChangeDescription(ASTChangeDescription changeDescription) {
        //do nothing
    }
}
