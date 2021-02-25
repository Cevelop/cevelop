package com.cevelop.templator.plugin.asttools.resolving;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPDeferredFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionTemplate;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.LookupData;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;


public final class FunctionCallResolver {

    private FunctionCallResolver() {}

    public static IFunction resolveCall(ICPPASTFunctionCallExpression functionCall, AbstractResolvedNameInfo parentInstance, ASTAnalyzer analyzer)
            throws TemplatorException, DOMException {
        IASTName functionCallName = ASTTools.getName(functionCall);
        IBinding binding = analyzer.resolveTargetBinding(functionCallName);

        if (binding instanceof ICPPDeferredFunction) {
            ICPPDeferredFunction deferredFunctionBinding = (ICPPDeferredFunction) binding;

            if (deferredFunctionBinding != null && deferredFunctionBinding.getCandidates() != null) {
                ICPPFunction[] candidateFunctions = ArrayUtil.removeNulls(deferredFunctionBinding.getCandidates());
                candidateFunctions = removeWrongFunctionTypeCandidates(functionCallName, candidateFunctions);

                if (candidateFunctions.length > 1) {
                    binding = resolveDeferredCall(functionCallName, candidateFunctions, null, parentInstance, analyzer);
                } else if (candidateFunctions.length == 1) {
                    binding = deferredFunctionBinding.getCandidates()[0];
                }
            } else {
                throw new TemplatorException("No function found for " + functionCallName +
                                             ". There are maybe compilation errors that first need to be fixed.");
            }
        }
        if (binding instanceof IFunction) {
            return (IFunction) binding;
        } else {
            throw new TemplatorException(functionCall.getRawSignature() + " resolved to " + binding.getClass() + " instead of a function.");
        }
    }

    /**
     * 14.8.1, note 4.
     *
     * Given the code
     *
     * <pre>
     *  template&lt;typename T&gt; void foo(T value) {}
     *
     * void foo(bool b) {}
     *
     * int main() { foo&lt;&gt;(true); }
     * </pre>
     *
     * The function call name from {@code foo<>(true)} should resolve to the function template and not the non-template
     * function {@code foo(bool b)}. The {@code bool} match would be better but because there is a template-id {@code
     * <>} is has to be a function template call.
     *
     * </br>
     * <b>This method removes non-template function candidates if the function call has a template-id.</b>
     */
    private static ICPPFunction[] removeWrongFunctionTypeCandidates(IASTName functionCallName, ICPPFunction[] originalCandidates) {
        ICPPFunction[] candidates = new ICPPFunction[originalCandidates.length];
        boolean isCertainlyFunctionTemplateCall = functionCallName.getParent() instanceof ICPPASTTemplateId;
        if (isCertainlyFunctionTemplateCall) {
            int i = 0;
            for (ICPPFunction candidate : originalCandidates) {
                if (candidate instanceof ICPPFunctionTemplate) {
                    candidates[i++] = candidate;
                }
            }
        } else {
            return originalCandidates;
        }

        return ArrayUtil.removeNulls(candidates);
    }

    public static IBinding resolveDeferredCall(IASTName functionCallName, ICPPFunction[] candidates, IType impliedObjectType,
            AbstractResolvedNameInfo parentInstance, ASTAnalyzer analyzer) throws TemplatorException {
        TemplateArgumentMap templateArgumentMap = parentInstance.getTemplateArgumentMap();
        if (templateArgumentMap != null) {
            LookupData templateContextData = TemplateContextLookupData.createLookupData(ASTTools.extractTemplateInstanceName(functionCallName),
                    templateArgumentMap, impliedObjectType);
            try {
                final IBinding resolvedFunction = CPPSemantics.resolveFunction(templateContextData, candidates, true, true);
                if (resolvedFunction != null) {
                    return resolvedFunction;
                }
            } catch (DOMException e) {
                throw new TemplatorException(functionCallName + " could not be resolved.", e);
            }
        } else {
            throw new TemplatorException(functionCallName + " could not be resolved because the template arguments could not be deduced.");
        }

        return null;
    }
}
