package com.cevelop.gslator.quickfixes.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.utils.ASTHelper;


@SuppressWarnings("restriction")
public class C60_03CopyAssignmentReturnByNonConstRefQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C60_03.getId())) {
            return Rule.C60 + ": Set return type to non-const reference";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, final ASTRewrite hRewrite) {
        markedNode = getSimpleDeclarationOrFunctionDefinition(markedNode);
        IASTDeclaration replacement = (IASTDeclaration) markedNode.copy(CopyStyle.withLocations);

        final IASTDeclSpecifier declSpec = ASTHelper.getDeclSpecifierFromDeclaration(replacement);

        if (!(declSpec instanceof ICPPASTNamedTypeSpecifier) && declSpec.toString().equals("void")) {
            final IASTDeclaration olDec = replacement;
            replacement = factory.newSimpleDeclaration(factory.newNamedTypeSpecifier(ASTHelper.getCompositeTypeSpecifier(markedNode).getName()
                    .copy()));
            setDeclarator(replacement, olDec);
        }

        final ICPPASTFunctionDeclarator declarator = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(replacement);

        declSpec.setConst(false);

        if (!(declarator.getPointerOperators().length == 1 && declarator.getPointerOperators()[0] instanceof ICPPASTReferenceOperator)) {
            declarator.addPointerOperator(factory.newReferenceOperator());
        }

        hRewrite.replace(markedNode, replacement, null);
    }

    private void setDeclarator(final IASTDeclaration replacement, final IASTDeclaration oldDec) {
        if (replacement instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition funcDef = (ICPPASTFunctionDefinition) replacement;
            funcDef.setDeclarator(((ICPPASTFunctionDefinition) oldDec).getDeclarator().copy(CopyStyle.withLocations));
        } else if (replacement instanceof IASTSimpleDeclaration) {
            final IASTSimpleDeclaration simDec = (IASTSimpleDeclaration) replacement;
            simDec.addDeclarator(((IASTSimpleDeclaration) oldDec).getDeclarators()[0].copy(CopyStyle.withLocations));
        }
    }

}
