package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.utils.ComplementaryNodeFactory;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


public class ArgMapping {

    private Arg[] args;

    public enum Arg {
        ARG_0, ARG_1, ARG_2, OFF_0, ZERO, NPOS, BEGIN, END
    }

    public ArgMapping(Arg... args) {
        this.args = args;
    }

    public IASTNode[] getOutArguments(IASTInitializerClause inArgs[], IASTIdExpression idExpression, Context context) {
        List<IASTNode> outArgs = new ArrayList<>();

        for (Arg arg : args) {
            IASTNode outArg = getOutArgument(arg, inArgs, idExpression, context);
            outArgs.add(outArg);
        }

        return outArgs.toArray(new IASTNode[] {});
    }

    private IASTNode getOutArgument(Arg arg, IASTInitializerClause inArgs[], IASTIdExpression idExpression, Context context) {
        switch (arg) {
        case ARG_0:
            return ASTAnalyzer.extractStdStringArg(inArgs[0]);
        case ARG_1:
            return ASTAnalyzer.extractStdStringArg(inArgs[1]);
        case ARG_2:
            return ASTAnalyzer.extractStdStringArg(inArgs[2]);
        case OFF_0:
            return context.getOffset(idExpression);
        case ZERO:
            return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newIntegerLiteral(0);
        case NPOS:
            return ComplementaryNodeFactory.newNposExpression(context.getStringType());
        case BEGIN:
            return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newMemberFunctionCallExpression(idExpression.getName(), StdString.BEGIN);
        case END:
            IASTExpression arg2 = (IASTExpression) inArgs[2];
            if (FunctionAnalyzer.isCallToMemberFunction(arg2, Function.SIZE)) {
                return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newMemberFunctionCallExpression(idExpression.getName(), StdString.END);
            } else {
                IASTExpression beginCall = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newMemberFunctionCallExpression(idExpression.getName(),
                        StdString.BEGIN);
                return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newPlusExpression(beginCall, arg2);
            }
        default:
            return null;
        }
    }
}
