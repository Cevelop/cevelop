package com.cevelop.gslator.checkers.ES40ToES64ExpressionRules;

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IResource;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.ES46AvoidLossyArithmeticConversionsVisitor;
import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.infos.GslatorInfo;


public class ES46AvoidLossyArithmeticConversionsChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Res-narrowing";

    @Override
    public void checkAst(IASTTranslationUnit ast) {
        ast.accept(new ES46AvoidLossyArithmeticConversionsVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.ES46;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES46;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }

    @Override
    public void initPreferences(IProblemWorkingCopy problem) {
        super.initPreferences(problem);
        addPreference(problem, ES46LossyType.FpToInt.toString(), "Floating Point to Integer conversions", Boolean.TRUE);
        addPreference(problem, ES46LossyType.FpToIntFunc.toString(), "Floating Point to Integer conversions in function arguments", Boolean.TRUE);
        addPreference(problem, ES46LossyType.IntToCharBig.toString(), "Integer (>= long) to Char conversions", Boolean.TRUE);
        addPreference(problem, ES46LossyType.IntToCharBigFunc.toString(), "Integer (>= long) to Char conversions in function arguments",
                Boolean.TRUE);
        addPreference(problem, ES46LossyType.IntToCharSmll.toString(), "Integer (< long) to Char", Boolean.FALSE);
        addPreference(problem, ES46LossyType.IntToCharSmllFunc.toString(), "Integer (< long) to Char in function arguments", Boolean.TRUE);
        addPreference(problem, ES46LossyType.Integer.toString(), "Narrowing Integer/Char conversions", Boolean.FALSE);
        addPreference(problem, ES46LossyType.IntegerFunc.toString(), "Narrowing Integer/Char conversions in function arguments", Boolean.TRUE);
        addPreference(problem, ES46LossyType.Fp.toString(), "Lossy Floating Point conversions", Boolean.FALSE);
        addPreference(problem, ES46LossyType.FpFunc.toString(), "Lossy Floating Point conversions in function arguments", Boolean.TRUE);
        addPreference(problem, ES46LossyType.ToUnsigned.toString(), "Signed to unsigned conversions", Boolean.FALSE);
        addPreference(problem, ES46LossyType.ToUnsignedFunc.toString(), "Signed to unsigned conversions in function arguments", Boolean.TRUE);
    }

    public void reportProblem(ES46LossyType type, IASTNode node) {
        if (isEnabled(type, node.getTranslationUnit().getOriginatingTranslationUnit().getResource())) {
            super.reportProblem(ProblemId.P_ES46, node,
                    new GslatorInfo(type.getMessage()));
        }
    }

    private boolean isEnabled(ES46LossyType type, IResource res) {
        return (boolean) getPreference(res, type.toString());
    }
}
