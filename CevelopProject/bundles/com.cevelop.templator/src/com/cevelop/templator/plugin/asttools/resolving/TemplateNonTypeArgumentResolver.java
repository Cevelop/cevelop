package com.cevelop.templator.plugin.asttools.resolving;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameterPackType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameterMap;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateNonTypeArgument;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateNonTypeParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation.ConstexprEvaluationContext;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.ActivationRecord;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalBinary;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalConditional;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalFunctionCall;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalUnary;


public class TemplateNonTypeArgumentResolver {

    private static List<EvalBinding> getEvalBindings(ICPPEvaluation cppEvaluation) {
        List<EvalBinding> evalBindings = new ArrayList<>();
        if (cppEvaluation instanceof EvalBinary) {
            EvalBinary evalBinary = (EvalBinary) cppEvaluation;
            evalBindings.addAll(getEvalBindings(evalBinary.getArg1()));
            evalBindings.addAll(getEvalBindings(evalBinary.getArg2()));
        } else if (cppEvaluation instanceof EvalConditional) {
            EvalConditional evalConditional = (EvalConditional) cppEvaluation;
            evalBindings.addAll(getEvalBindings(evalConditional.getCondition()));
            evalBindings.addAll(getEvalBindings(evalConditional.getNegative()));
            evalBindings.addAll(getEvalBindings(evalConditional.getPositive()));
        } else if (cppEvaluation instanceof EvalUnary) {
            EvalUnary evalUnary = (EvalUnary) cppEvaluation;
            evalBindings.addAll(getEvalBindings(evalUnary.getArgument()));
        } else if (cppEvaluation instanceof EvalFunctionCall) {
            EvalFunctionCall evalFunctionCall = (EvalFunctionCall) cppEvaluation;
            for (ICPPEvaluation evalArg : evalFunctionCall.getArguments()) {
                evalBindings.addAll(getEvalBindings(evalArg));
            }
        } else if (cppEvaluation instanceof EvalBinding) {
            EvalBinding evalBinding = (EvalBinding) cppEvaluation;
            evalBindings.add(evalBinding);
        }
        return evalBindings;
    }

    public static CPPTemplateNonTypeArgument getEvaluatedArgument(CPPTemplateNonTypeArgument nonTypeArg, ICPPTemplateParameterMap templateParamMap) {
        ICPPEvaluation nonTypeEvaluation = nonTypeArg.getNonTypeEvaluation();
        List<EvalBinding> evalBindings = getEvalBindings(nonTypeEvaluation);
        ActivationRecord record = new ActivationRecord();
        ConstexprEvaluationContext context = new ConstexprEvaluationContext();
        for (EvalBinding evalBinding : evalBindings) {
            IBinding binding = evalBinding.getBinding();
            if (binding instanceof CPPTemplateNonTypeParameter) {
                CPPTemplateNonTypeParameter parameter = (CPPTemplateNonTypeParameter) binding;
                ICPPTemplateArgument templateArgument = null;
                if (parameter.isParameterPack()) {
                    ICPPParameterPackType parameterPack = (ICPPParameterPackType) parameter.getType();
                    ICPPTemplateParameter templateParameter = (ICPPTemplateParameter) parameterPack.getType();
                    templateArgument = templateParamMap.getArgument(templateParameter);
                    CPPTemplateNonTypeArgument newNonTypeArgument;
                    //TODO handle parameter packs
                } else {
                    templateArgument = templateParamMap.getArgument(parameter);
                }
                ICPPEvaluation eval = templateArgument.getNonTypeEvaluation();
                record.update(binding, eval);
            }
        }
        ICPPEvaluation resEval = nonTypeEvaluation.computeForFunctionCall(record, context);
        IValue value = resEval.getValue();
        IType type = resEval.getType();
        if (value.numberValue() != null) {
            CPPTemplateNonTypeArgument newNonTypeArgument = new CPPTemplateNonTypeArgument(value, type);
            return newNonTypeArgument;
        }
        return null;
    }
}
