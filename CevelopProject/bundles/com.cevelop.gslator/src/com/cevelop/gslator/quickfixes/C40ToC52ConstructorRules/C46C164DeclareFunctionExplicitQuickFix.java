package com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C46C164DeclareFunctionExplicitQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C46.getId())) {
            return Rule.C46 + ": Set to explicit";
        }
        if (problemId.contentEquals(((IProblemId<?>) ProblemId.P_C164).getId())) {
            return Rule.C164.toString() + ": Set to explicit";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final IASTNode node = markedNode.getParent();
        final ICPPASTSimpleDeclSpecifier simpleDec = getSimpleDecl(node);
        final ICPPASTSimpleDeclSpecifier newSimpledec = simpleDec.copy(CopyStyle.withLocations);
        newSimpledec.setExplicit(true);
        hRewrite.replace(simpleDec, newSimpledec, null);
    }

    private ICPPASTSimpleDeclSpecifier getSimpleDecl(final IASTNode node) {
        final ICPPASTSimpleDeclSpecifier simpleDec;
        if (node instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition funcDec = (ICPPASTFunctionDefinition) node;
            simpleDec = (ICPPASTSimpleDeclSpecifier) funcDec.getDeclSpecifier();
        } else {
            final IASTSimpleDeclaration simDec = (IASTSimpleDeclaration) node;
            simpleDec = (ICPPASTSimpleDeclSpecifier) simDec.getDeclSpecifier();
        }
        return simpleDec;
    }

}
