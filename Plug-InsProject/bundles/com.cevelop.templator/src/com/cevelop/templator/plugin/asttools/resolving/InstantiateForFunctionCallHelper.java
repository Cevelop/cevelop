package com.cevelop.templator.plugin.asttools.resolving;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTExpression.ValueCategory;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameterMap;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateNonTypeArgument;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.ReflectionMethodHelper;


/**
 * Helper class to call
 * org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates.
 * instantiateForFunctionCall(ICPPFunctionTemplate, ICPPTemplateArgument[],
 * {@code List<IType>}, {@code List<ValueCategory>}, boolean, IASTNode) via reflection since its
 * private.
 */
public final class InstantiateForFunctionCallHelper {

    private InstantiateForFunctionCallHelper() {}

    public static ICPPSpecialization instantiateForFunctionCall(ICPPFunctionTemplate functionTemplate, ICPPASTFunctionCallExpression call,
            ICPPTemplateParameterMap parentParameterMap) throws TemplatorException {
        ICPPFunctionTemplate template = functionTemplate; // done
        ICPPTemplateArgument[] tmplArgs = null; // done
        List<IType> fnArgs = null; // done
        List<ValueCategory> argCats = null; // done

        // temp
        ICPPEvaluation[] functionArgs = null;

        IASTNode lookupPoint = ASTTools.getName(call);
        IASTNode parentOfPoint = lookupPoint.getParent();

        CPPSemantics.pushLookupPoint(lookupPoint);
        try {
            // get the tmplArgs
            if (parentOfPoint instanceof ICPPASTTemplateId) {
                try {
                    tmplArgs = CPPTemplates.createTemplateArgumentArray((ICPPASTTemplateId) parentOfPoint);
                    for (int i = 0; i < tmplArgs.length; i++) {
                        ICPPTemplateArgument arg = tmplArgs[i];
                        if (arg.getOriginalTypeValue() instanceof ICPPTemplateParameter) {
                            tmplArgs[i] = parentParameterMap.getArgument((ICPPTemplateParameter) tmplArgs[0].getOriginalTypeValue());
                        } else if (arg.isNonTypeValue()) {
                            tmplArgs[i] = TemplateNonTypeArgumentResolver.getEvaluatedArgument((CPPTemplateNonTypeArgument) arg, parentParameterMap);
                        }
                    }
                } catch (DOMException e) {
                    throw new TemplatorException(e);
                }
                lookupPoint = parentOfPoint;
            }
        } finally {
            CPPSemantics.popLookupPoint();
        }

        CPPSemantics.pushLookupPoint(lookupPoint);
        try {
            // get the fnArgs
            IASTInitializerClause[] arguments = call.getArguments();
            fnArgs = new ArrayList<>(arguments.length);
            functionArgs = new ICPPEvaluation[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                IASTInitializerClause clause = arguments[i];
                ICPPEvaluation eval = ((ICPPASTInitializerClause) clause).getEvaluation();
                functionArgs[i] = eval;

                IType type = SemanticUtil.getSimplifiedType(eval.getType());
                if (type instanceof ICPPTemplateParameter) {
                    ICPPTemplateArgument argument = parentParameterMap.getArgument((ICPPTemplateParameter) type);
                    type = argument.getTypeValue();
                }
                fnArgs.add(type);
            }

            // get the argCats
            if (functionArgs != null) {
                argCats = new ArrayList<>(functionArgs.length);
                for (final ICPPEvaluation arg : functionArgs) {
                    argCats.add(arg.getValueCategory());
                }
            }

            Method instantiateForFunctionCall = ReflectionMethodHelper.getNonAccessibleMethod(CPPTemplates.class, "instantiateForFunctionCall",
                    ICPPFunctionTemplate.class, ICPPTemplateArgument[].class, List.class, List.class, boolean.class);
            Object result = ReflectionMethodHelper.invokeStaticMethod(instantiateForFunctionCall, template, tmplArgs, fnArgs, argCats, false);

            if (result instanceof ICPPSpecialization) {
                return (ICPPSpecialization) result;
            }
        } catch (Exception e) {
            throw new TemplatorException(e);
        } finally {
            CPPSemantics.popLookupPoint();
        }
        return null;
    }

}
