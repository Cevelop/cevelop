package com.cevelop.gslator.checkers.visitors.C80toC89OtherDefaultOperationRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.gslator.CCGlator;
import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;
import com.cevelop.gslator.utils.ASTHelper.AnalyseSwapFunction;


@SuppressWarnings("restriction")
public class C85NamespaceLevelSwapFunctionVisitor extends BaseVisitor {

    private static final String SWAP = "swap";

    public C85NamespaceLevelSwapFunctionVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {

            final ICPPASTCompositeTypeSpecifier typeSpecifier = (ICPPASTCompositeTypeSpecifier) declSpec;
            if (!nodeHasNoIgnoreAttribute(this, typeSpecifier)) {
                return PROCESS_CONTINUE;
            }

            List<IASTDeclaration> memberFunctions = ASTHelper.collectMemberFunctions((ICPPASTCompositeTypeSpecifier) declSpec);

            boolean hasSwapFunction = false;
            for (IASTDeclaration memberFunction : memberFunctions) {
                if (ASTHelper.getSwapFunctionType(memberFunction, "") == AnalyseSwapFunction.IsMemberFunction) {
                    hasSwapFunction = true;
                }
                if (ASTHelper.getSwapFunctionType(memberFunction, "") == AnalyseSwapFunction.IsFriendFunction) {
                    return PROCESS_CONTINUE;
                }
            }

            if (hasSwapFunction && !hasASTSwapFunction(typeSpecifier) && !betterHasIndexSwapFunction(typeSpecifier)) {
                checker.reportProblem(ProblemId.P_C85, typeSpecifier.getName());
            }

        }
        return super.visit(declSpec);
    }

    private boolean hasASTSwapFunction(ICPPASTCompositeTypeSpecifier typeSpecifier) {
        ICPPASTNamespaceDefinition nameSpace = ASTHelper.getNamespace((IASTDeclaration) typeSpecifier.getParent());
        IASTDeclaration[] declarations;
        if (nameSpace == null) {
            declarations = typeSpecifier.getTranslationUnit().getDeclarations();
        } else {
            declarations = nameSpace.getDeclarations();

        }
        for (IASTDeclaration declaration : declarations) {
            if (ASTHelper.getSwapFunctionType(declaration, typeSpecifier.getName().toString()) == AnalyseSwapFunction.IsNamespaceFunction) {
                return true;
            }
        }
        return false;
    }

    private static boolean betterHasIndexSwapFunction(ICPPASTCompositeTypeSpecifier typeSpecifier) {
        IBinding classBinding = typeSpecifier.getName().resolveBinding();
        IType classType = (IType) classBinding;
        try {
            IScope classScope = classBinding.getScope();
            IBinding[] swapBindings = classScope.find(SWAP, typeSpecifier.getTranslationUnit());
            for (IBinding swapBinding : swapBindings) {
                if (swapBinding instanceof IFunction && swapBinding.getScope() == classScope) {
                    IFunction functionBinding = (IFunction) swapBinding;
                    IFunctionType functionType = functionBinding.getType();
                    IType[] parametertypes = functionType.getParameterTypes();
                    if (parametertypes.length == 2) {
                        return swapParameterHasCorrectType(parametertypes[0], classType) && swapParameterHasCorrectType(parametertypes[1], classType);
                    }
                }
            }
        } catch (DOMException e) {
            CCGlator.getDefault().logException(e);
        }

        return false;
    }

    private static boolean swapParameterHasCorrectType(IType parameterType, IType expectedType) {
        IType untypedefedParameterType = SemanticUtil.getNestedType(parameterType, SemanticUtil.TDEF);
        if (!(untypedefedParameterType instanceof ICPPReferenceType)) {
            return false;
        }
        if (SemanticUtil.isConst(untypedefedParameterType)) {
            return false;
        }
        IType nakedParameterType = SemanticUtil.getUltimateTypeUptoPointers(untypedefedParameterType);
        return nakedParameterType.isSameType(expectedType);
    }
}
