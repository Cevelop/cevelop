package com.cevelop.gslator.quickfixes;

import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTAttributeList;
import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAttributeList;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.util.AttributeMatcher;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


@SuppressWarnings("restriction")
public class SetAttributeQuickFix extends BaseQuickFix {

    private ProblemId problemId;

    @Override
    public String getLabel() {
        setProblemIdIfIsNull();
        return "Set ignore Attribute for Rule: " + getRuleSubString();
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) {
            return false;
        }
        setProblemIdIfIsNull();
        return BaseChecker.getCheckerByProblemId(problemId).isIgnoreApplicable(marker, getMarkedNode(marker));
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        setProblemIdIfIsNull();
        BaseChecker checker = BaseChecker.getCheckerByProblemId(problemId);
        String ignorestring = "\"" + checker.getIgnoreString() + "\"";

        final IASTNode oldAttribNode = checker.getIgnoreAttributeNode(markedNode);
        final IASTNode newAttribNode = copyOldNode(oldAttribNode);

        modifyAttributeSpec((IASTAttributeOwner) newAttribNode, ignorestring);

        hRewrite.replace(oldAttribNode, newAttribNode, null);
    }

    private IASTNode copyOldNode(final IASTNode oldAttribNode) {

        if (oldAttribNode instanceof IASTExpressionStatement) {
            // Workaround for GCC because it fails to compile if an expression has an attribute
            // https://gcc.gnu.org/bugzilla/show_bug.cgi?id=71790
            final IASTCompoundStatement newCompoundStatement = factory.newCompoundStatement();
            newCompoundStatement.addStatement((IASTExpressionStatement) oldAttribNode.copy(CopyStyle.withLocations));
            newCompoundStatement.setParent(oldAttribNode.getParent());
            return newCompoundStatement;
        }

        return oldAttribNode.copy(CopyStyle.withLocations);
    }

    private void setProblemIdIfIsNull() {
        if (problemId == null) {
            problemId = ProblemId.of(getProblemId(marker));
        }
    }

    private void modifyAttributeSpec(final IASTAttributeOwner decl, final String ruleNr) {
        decl.addAttributeSpecifier(createNewAttributeSpecifier(ruleNr));
    }

    private IASTAttributeList createNewAttributeSpecifier(final String ruleNr) {
        final ICPPASTAttributeList newAttrList = factory.newAttributeList();
        newAttrList.addAttribute(createNewAttr(ruleNr));
        return newAttrList;
    }

    private IASTAttribute createNewAttr(final String ruleNr) {
        final IASTAttribute attr = factory.newAttribute(AttributeMatcher.IGNORE_ATTRIBUTE.toCharArray(), factory.newToken(0, ruleNr.toCharArray()));
        return attr;
    }

    private String getRuleSubString() {
        return BaseChecker.getCheckerByProblemId(problemId).getRule().toString();
    }
}
