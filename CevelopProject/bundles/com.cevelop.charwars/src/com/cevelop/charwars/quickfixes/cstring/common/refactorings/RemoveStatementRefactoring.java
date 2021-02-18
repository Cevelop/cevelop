package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTIdExpression;

import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;


public class RemoveStatementRefactoring extends Refactoring {

    private Function inFunction;

    public RemoveStatementRefactoring(Function inFunction, EnumSet<Kind> contextKinds) {
        this.inFunction = inFunction;
        setContextKinds(contextKinds);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        if (FunctionAnalyzer.isFunctionCallArg(idExpression, 0, inFunction)) {
            makeApplicable(null);
        }
    }

    @Override
    protected void updateChangeDescription(ASTChangeDescription changeDescription) {
        changeDescription.setRemoveStatement(true);
    }
}
