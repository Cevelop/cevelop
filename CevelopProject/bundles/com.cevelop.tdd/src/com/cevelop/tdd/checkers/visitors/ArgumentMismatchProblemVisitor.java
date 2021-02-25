package com.cevelop.tdd.checkers.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.parser.util.ArrayUtil;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.ArgumentMismatchInfo;
import com.cevelop.tdd.quickfixes.argument.ArgumentMismatchQuickfixGenerator;
import com.cevelop.tdd.refactorings.argument.ArgumentRefactoring;


public class ArgumentMismatchProblemVisitor extends AbstractResolutionProblemVisitor {

    private static final String EMPTY_STRING = "";
    private static final String COMMA_SPACE  = ", ";

    private Consumer3<IProblemId<ProblemId>, IASTName, ArgumentMismatchInfo> problemReporter;

    public ArgumentMismatchProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTName, ArgumentMismatchInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    protected void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name) {
        if (!TddHelper.isMethod(name) || problemBinding.getCandidateBindings().length < 1) {
            return;
        }
        IASTFunctionCallExpression call = TddHelper.getAncestorOfType(name, IASTFunctionCallExpression.class);
        List<IASTInitializerClause> oldArgs = Arrays.asList(call.getArguments());
        IBinding[] candidates = ArrayUtil.trim(problemBinding.getCandidateBindings());
        candidates = removeDuplicates(candidates);
        String contextString = EMPTY_STRING;
        int argNr;
        for (argNr = 0; argNr < candidates.length; argNr++) {
            IBinding b = candidates[argNr];
            if (b instanceof ICPPFunction) {
                ICPPFunction candidate = (ICPPFunction) b;
                List<ICPPParameter> newParams = Arrays.asList(candidate.getParameters());
                List<IASTInitializerClause> newArgs = ArgumentRefactoring.getNewArguments(oldArgs, newParams);
                if (newArgs == null || newArgs.size() == oldArgs.size()) {
                    return;
                }
                if (argNr > 0) {
                    contextString += ":candidate ";
                }
                contextString += getContextString(name, candidate);
            }
        }
        if (candidates.length == 0) {
            return;
        }
        String missingName = new String(name.getLastName().getSimpleID());
        String message = missingName;
        ArgumentMismatchInfo info = new ArgumentMismatchInfo();
        info.argumentName = missingName;
        info.message = message;
        info.candidateNr = argNr;
        info.candidates = contextString;
        problemReporter.accept(ProblemId.ARGUMENT_MISMATCH, name.getLastName(), info);
    }

    private String getContextString(IASTName name, ICPPFunction candidate) {
        return getArgumentNames(name, candidate) + ArgumentMismatchQuickfixGenerator.SEPARATOR + getParameterNames(name, candidate);
    }

    private String getArgumentNames(IASTName name, ICPPFunction candidate) {
        String result = EMPTY_STRING;
        IASTFunctionCallExpression call = TddHelper.getAncestorOfType(name, IASTFunctionCallExpression.class);
        List<IASTInitializerClause> oldArgs = Arrays.asList(call.getArguments());
        ICPPParameter[] newParams = candidate.getParameters();
        if (oldArgs.size() > newParams.length) {
            result += ArgumentMismatchQuickfixGenerator.REMOVE_ARGUMENTS;
            for (int i = 0; i < oldArgs.size(); i++) {
                if (i >= newParams.length) {
                    result += ASTTypeUtil.getType(TypeHelper.getTypeOf(oldArgs.get(i))) + COMMA_SPACE;
                }
            }
        } else {
            result = ArgumentMismatchQuickfixGenerator.ADD_ARGUMENTS;
            for (int i = 0; i < newParams.length; i++) {
                if (i >= oldArgs.size()) {
                    result += ASTTypeUtil.getType(newParams[i].getType()) + COMMA_SPACE;
                }
            }
        }
        if (result.endsWith(COMMA_SPACE)) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

    private String getParameterNames(IASTName name, ICPPFunction candidate) {
        String result = ArgumentMismatchQuickfixGenerator.PARAMETERS;
        if (candidate.getParameters().length == 0) {
            return result;
        }
        IBinding binding = name.resolveBinding();
        if (binding instanceof IProblemBinding) {
            for (ICPPParameter p : candidate.getParameters()) {
                result += ASTTypeUtil.getType(p.getType()) + COMMA_SPACE;
            }
        }
        return result.substring(0, result.length() - 2);
    }

    // There are two markers generated
    private IBinding[] removeDuplicates(IBinding[] candidates) {
        ArrayList<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < candidates.length; i++) {
            for (int j = i + 1; j < candidates.length; j++) {
                ICPPParameter[] arglist1 = ((ICPPFunction) candidates[i]).getParameters();
                ICPPParameter[] arglist2 = ((ICPPFunction) candidates[j]).getParameters();
                boolean same = false;
                if (arglist1.length == 0 && arglist2.length == 0) {
                    same = true;
                    continue;
                }
                for (int k = 0; arglist1.length == arglist2.length && k < arglist1.length; k++) {
                    ICPPParameter param1 = arglist1[k];
                    ICPPParameter param2 = arglist2[k];
                    same = param1.getType().isSameType(param2.getType());
                    if (!same) {
                        break;
                    }
                }
                if (same) {
                    toRemove.add(j);
                }
            }
        }
        ArrayList<ICPPBinding> result = new ArrayList<>();
        for (int i = 0; i < candidates.length; i++) {
            if (!toRemove.contains(i)) {
                result.add((ICPPBinding) candidates[i]);
            }
        }
        return result.toArray(new IBinding[result.size()]);
    }
}
