package com.cevelop.gslator.quickfixes.C30ToC37DestructorRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.utils.ASTHelper;


public class C35BaseClassDestructorQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C35.getId())) return Rule.C35 + ": Set destructor to public virtual or protected non-virtual";
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTFunctionDefinition oldDestructor = (ICPPASTFunctionDefinition) markedNode.getParent();
        final ICPPASTFunctionDefinition newdestructor = oldDestructor.copy(CopyStyle.withLocations);
        final int visibility = ASTHelper.getVisibilityForStatement(oldDestructor);

        rewriteDependingOnVisibilityLabel(hRewrite, oldDestructor, newdestructor, visibility);
    }

    private void rewriteDependingOnVisibilityLabel(final ASTRewrite hRewrite, final ICPPASTFunctionDefinition oldDestructor,
            final ICPPASTFunctionDefinition newdestructor, final int visibility) {
        switch (visibility) {
        case ICPPASTVisibilityLabel.v_public:
            ((ICPPASTSimpleDeclSpecifier) newdestructor.getDeclSpecifier()).setVirtual(true);
            hRewrite.replace(oldDestructor, newdestructor, null);
            break;
        case ICPPASTVisibilityLabel.v_protected:
            ((ICPPASTSimpleDeclSpecifier) newdestructor.getDeclSpecifier()).setVirtual(false);
            hRewrite.replace(oldDestructor, newdestructor, null);
            break;
        case ICPPASTVisibilityLabel.v_private:
            ((ICPPASTSimpleDeclSpecifier) newdestructor.getDeclSpecifier()).setVirtual(false);
            final List<IASTNode> nodesToAdd = new ArrayList<>();
            nodesToAdd.add(newdestructor);
            insertNodesUnderVisibilityLabel(hRewrite, ASTHelper.getCompositeTypeSpecifier(oldDestructor), nodesToAdd,
                    ICPPASTVisibilityLabel.v_protected);
            hRewrite.remove(oldDestructor, null);
            break;
        default:
            throw new RuntimeException("Default case in C35BaseClassDestructorQuickFix in rewriteDependingOnVisibilityLabel");

        }
    }

}
