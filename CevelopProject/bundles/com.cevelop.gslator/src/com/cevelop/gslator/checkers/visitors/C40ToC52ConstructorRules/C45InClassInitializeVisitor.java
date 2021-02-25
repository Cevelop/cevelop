package com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.infos.GslatorInfo;
import com.cevelop.gslator.utils.ASTHelper;
import com.cevelop.gslator.utils.ASTHelper.SpecialFunction;


public class C45InClassInitializeVisitor extends BaseVisitor {

    final private static String MSG_DEFAULT      = "In-class member variables should be default initialized";
    final private static String MSG_FORCONSTVALS = "Default constructor shouldn't only initialize data members";

    public C45InClassInitializeVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            final ICPPASTCompositeTypeSpecifier struct = (ICPPASTCompositeTypeSpecifier) declSpec;
            final IASTDeclaration ctorDec = ASTHelper.getFirstSpecialMemberFunction(struct, SpecialFunction.DefaultConstructor);

            if (ctorDec != null && nodeHasNoIgnoreAttribute(this, ctorDec) && nodeHasNoIgnoreAttribute(this, struct)) {
                reportProblem(struct, ctorDec);
            }

        }
        return super.visit(declSpec);
    }

    protected void reportProblem(final ICPPASTCompositeTypeSpecifier struct, final IASTDeclaration ctorDec) {
        if (ctorDec instanceof ICPPASTFunctionDefinition) {
            report(struct, ctorDec);
        } else if (ctorDec instanceof IASTSimpleDeclaration) {
            final ICPPASTFunctionDefinition impl = getImplFromDeclaration((IASTSimpleDeclaration) ctorDec);
            reportFunctionDefinition(struct, ctorDec, impl);
        }
    }

    protected void reportFunctionDefinition(final ICPPASTCompositeTypeSpecifier struct, final IASTDeclaration ctorDec,
            final ICPPASTFunctionDefinition impl) {
        if (impl != null) {
            report(struct, impl);
        } else {
            report(struct, ctorDec);
        }
    }

    private void report(final ICPPASTCompositeTypeSpecifier struct, final IASTDeclaration ctorDec) {
        final List<IASTSimpleDeclaration> memVars = ASTHelper.collectMemberVariables(struct);
        report(ctorDec, memVars);
    }

    protected void report(final IASTDeclaration ctor, final List<IASTSimpleDeclaration> memVars) {
        if (!hasOnlyConstInitVal(ctor) && !memVarsAreInited(memVars)) {
            checker.reportProblem(ProblemId.P_C45, ASTHelper.getCompositeTypeSpecifier(memVars.get(0)).getName(), new GslatorInfo(MSG_DEFAULT));
        }
        if (hasOnlyConstInitVal(ctor)) {
            final IASTDeclaration ctorHeader = getCtorInStruct(memVars);
            if (ctorHeader instanceof IASTSimpleDeclaration) {
                checker.reportProblem(ProblemId.P_C45, ((IASTSimpleDeclaration) ctorHeader).getDeclarators()[0], new GslatorInfo(MSG_FORCONSTVALS));
            } else if (ctorHeader != null) {
                checker.reportProblem(ProblemId.P_C45, ((IASTFunctionDefinition) ctorHeader).getDeclarator(), new GslatorInfo(MSG_FORCONSTVALS));
            }
        }
    }

    private boolean memVarsAreInited(final List<IASTSimpleDeclaration> memVars) {
        for (final IASTSimpleDeclaration var : memVars) {
            for (final IASTDeclarator decl : var.getDeclarators()) {
                final IASTInitializer initVal = decl.getInitializer();
                if (initVal == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private IASTDeclaration getCtorInStruct(final List<IASTSimpleDeclaration> memVars) {
        if (memVars.size() > 0) {
            final ICPPASTCompositeTypeSpecifier struct = ASTHelper.getCompositeTypeSpecifier(memVars.get(0));
            return ASTHelper.getFirstSpecialMemberFunction(struct, SpecialFunction.DefaultConstructor);
        }
        return null;
    }
}
