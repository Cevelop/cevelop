package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.utils.ASTHelper;


public class ES50_02RemoveConstCastAndMakeMembervarMutableQuickFix extends ES50_00RemoveConstCastQuickFix {

    @Override
    public boolean isApplicable(IMarker marker) {
        if (super.isApplicable(marker)) {
            IASTCastExpression markedNode = (IASTCastExpression) getMarkedNode(marker);
            if(markedNode == null) {
            	return false;
            }
            IIndex index = markedNode.getTranslationUnit().getIndex();
            try {
                index.acquireReadLock();
                IType type = ASTHelper.getTypeFromExpressionElement(markedNode.getOperand());
                if (!ASTHelper.isTypeConst(type) && isFunctionConst(markedNode) && isOperandMemberOfClass(markedNode)) { // Case "func is const" (membervar can be mutable)
                    IASTExpression op = markedNode.getOperand();
                    if (op instanceof IASTIdExpression) {
                        if (isFileWithDeclarationOfNameModifiable(((IASTIdExpression) op).getName())) return true;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                index.releaseReadLock();
            }
        }
        return false;
    }

    @Override
    public String getLabel() {
        return Rule.ES50 + ": make member variable mutable and remove const cast";
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTCastExpression constcast = (IASTCastExpression) markedNode;
        IASTNode operand = constcast.getOperand();
        if (operand instanceof IASTIdExpression) {
            setMemberVariableMutable(((IASTIdExpression) operand).getName(), hRewrite);
            removeConstCast(constcast, hRewrite);
        }
    }

    private void setMemberVariableMutable(IASTName opername, ASTRewrite hRewrite) {
        IASTDeclSpecifier declspec = ASTHelper.getDeclSpecifierFromDeclaration(ASTHelper.getDeclaration(ASTHelper.findDeclaratorToName(opername,
                astRewriteStore)));
        IASTDeclSpecifier newdeclspec = declspec.copy();
        newdeclspec.setStorageClass(IASTDeclSpecifier.sc_mutable);
        ASTRewrite rewr = hRewrite;
        if (!(opername.getTranslationUnit().equals(declspec.getTranslationUnit()))) {
            rewr = astRewriteStore.getASTRewrite(declspec);
        }
        rewr.replace(declspec, newdeclspec, null);
    }
}
