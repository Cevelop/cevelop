package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotUseTypedefVisitor extends CodeAnalysatorVisitor {

    public DoNotUseTypedefVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration decl) {
        if (decl instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration sd = (IASTSimpleDeclaration) decl;
            if (sd.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                String contextFlagString = "";
                if (isTypeDefStruct(sd)) {
                    contextFlagString = ContextFlagsHelper.DoNotUseTypedefContextFlagContextFlagStruct;
                }
                reportRuleForNode(decl, contextFlagString);
            }
        }

        return super.visit(decl);
    }

    private boolean isTypeDefStruct(IASTSimpleDeclaration sd) {
        IASTDeclSpecifier declSpec = sd.getDeclSpecifier();
        if (!(declSpec instanceof ICPPASTElaboratedTypeSpecifier)) {
            return false;
        }
        ICPPASTElaboratedTypeSpecifier elabTypeSpec = (ICPPASTElaboratedTypeSpecifier) declSpec;
        return elabTypeSpec.getKind() == IASTElaboratedTypeSpecifier.k_struct;
    }
}
