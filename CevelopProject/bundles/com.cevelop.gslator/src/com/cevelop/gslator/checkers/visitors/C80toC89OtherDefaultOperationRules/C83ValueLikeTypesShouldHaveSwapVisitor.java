package com.cevelop.gslator.checkers.visitors.C80toC89OtherDefaultOperationRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.infos.GslatorInfo;
import com.cevelop.gslator.utils.ASTHelper;
import com.cevelop.gslator.utils.ASTHelper.AnalyseSwapFunction;


public class C83ValueLikeTypesShouldHaveSwapVisitor extends BaseVisitor {

    public C83ValueLikeTypesShouldHaveSwapVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(final IASTDeclaration declaration) {
        final IASTNode[] declchildren = declaration.getChildren();
        if (declchildren.length == 1 && declchildren[0] instanceof ICPPASTCompositeTypeSpecifier) {
            final ICPPASTCompositeTypeSpecifier typespec = (ICPPASTCompositeTypeSpecifier) declchildren[0];

            if (!nodeHasNoIgnoreAttribute(this, typespec)) {
                return PROCESS_CONTINUE;
            }

            if (!hasMemberVariables(typespec)) {
                return PROCESS_CONTINUE;
            }

            if (hasBase(typespec)) {
                return PROCESS_CONTINUE;
            }

            List<ICPPASTFunctionDeclarator> wankyswaps = new ArrayList<>();
            if (hasMemberSwapOrVirtualFunction(typespec, wankyswaps)) {
                return PROCESS_CONTINUE;
            }

            reportProblems(typespec, wankyswaps);
        }
        return super.visit(declaration);
    }

    private boolean hasMemberSwapOrVirtualFunction(final ICPPASTCompositeTypeSpecifier typespec, List<ICPPASTFunctionDeclarator> wankyswaps) {
        List<IASTDeclaration> functions = ASTHelper.collectMemberFunctions(typespec);
        for (IASTDeclaration function : functions) {
            List<AnalyseSwapFunction> whynot = ASTHelper.analyseSwapFunction(function, "");
            if (whynot.size() == 1 && whynot.get(0) == AnalyseSwapFunction.IsMemberFunction) {
                return true;
            }

            if (whynot.size() == 2 && whynot.contains(AnalyseSwapFunction.IsMemberFunction) && whynot.contains(
                    AnalyseSwapFunction.ParamIsNotReference)) {
                ICPPASTFunctionDeclarator funcdeclarator = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(function);
                wankyswaps.add(funcdeclarator);
            }
            if (ASTHelper.getDeclSpecifierFromDeclaration(function) instanceof ICPPASTDeclSpecifier) {
                if (((ICPPASTDeclSpecifier) (ASTHelper.getDeclSpecifierFromDeclaration(function))).isVirtual()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasMemberVariables(final ICPPASTCompositeTypeSpecifier typespec) {
        List<IASTSimpleDeclaration> variables = ASTHelper.collectMemberVariables(typespec);
        return variables.size() != 0;
    }

    private boolean hasBase(final ICPPASTCompositeTypeSpecifier typespec) {
        for (IASTNode iastNode : typespec.getChildren()) {
            if (iastNode instanceof ICPPASTBaseSpecifier) {
                return true;
            }
        }
        return false;
    }

    private void reportProblems(final ICPPASTCompositeTypeSpecifier typespec, List<ICPPASTFunctionDeclarator> wankyswaps) {
        for (ICPPASTFunctionDeclarator wankyswap : wankyswaps) {
            if (nodeHasNoIgnoreAttribute(this, wankyswap)) {
                checker.reportProblem(ProblemId.P_C83, wankyswap, new GslatorInfo("Swap Function Parameter has to be Reference"));
            }
        }

        checker.reportProblem(ProblemId.P_C83, typespec.getName(), new GslatorInfo("Value-like types should have a swap member function."));
    }
}
